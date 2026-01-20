package com.spring.batch.config;

import com.spring.batch.processor.SimpleProcessor;
import com.spring.batch.reader.SimpleReader;
import com.spring.batch.writer.SimpleWriter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuración avanzada de Spring Batch para un proyecto con un único Job.
 *
 * Este Job realiza procesamiento por chunks, soporta paralelismo, tolerancia a errores
 * y logging de ejecución de Steps. Es ideal como base para un flujo batch simple
 * que luego se puede escalar a múltiples Steps o Jobs.
 *
 * <p>Características incluidas en esta configuración:
 * <ul>
 *     <li><b>Chunks:</b> procesamos un conjunto de 5 elementos por transacción para mejorar rendimiento.</li>
 *     <li><b>Paralelismo:</b> los chunks se ejecutan en varios hilos usando AsyncTaskExecutor.</li>
 *     <li><b>Retry y Skip:</b> los elementos que fallen se reintentan hasta 3 veces y se permiten hasta 5 elementos fallidos por Step.</li>
 *     <li><b>Logging:</b> se registran eventos antes y después de cada Step, incluyendo lecturas y escrituras realizadas.</li>
 * </ul>
 *
 * <p>Esta clase sirve de plantilla para agregar más Steps o Jobs en el futuro.
 */
@Configuration
public class JobConfig {

    /**
     * Repositorio de Jobs de Spring Batch. Necesario para crear Jobs y Steps
     * y mantener el historial de ejecuciones.
     */
    private final JobRepository jobRepository;

    /**
     * Constructor que inyecta el JobRepository.
     *
     * @param jobRepository repositorio de Jobs de Spring Batch
     */
    public JobConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // ======================= JOB =======================
    /**
     * Job principal llamado "jobOne".
     *
     * Este Job ejecuta únicamente un Step: stepOne.
     * Spring Batch mantiene el estado del Job y permite reinicios automáticos
     * en caso de fallo o interrupción.
     *
     * @param stepOne el Step principal del Job
     * @return instancia del Job
     */
    @Bean
    public Job jobOne(Step stepOne) {
        return new JobBuilder("jobOne", jobRepository)
                .start(stepOne) // indicamos explícitamente qué Step ejecutar primero
                .build();
    }

    // ======================= STEP =======================
    /**
     * Step principal del Job.
     *
     * Este Step es el núcleo del procesamiento:
     * - Procesa elementos en chunks de 5
     * - Cada chunk se puede ejecutar en paralelo según los hilos del TaskExecutor
     * - Tiene tolerancia a errores con retry y skip
     * - Incluye logging antes y después de la ejecución
     *
     * @param reader       Reader que obtiene los datos de origen
     * @param processor    Processor que transforma o valida los datos
     * @param writer       Writer que persiste los datos transformados
     * @param taskExecutor Executor para paralelismo de chunks
     * @return instancia del Step
     */
    @Bean
    public Step stepOne(SimpleReader reader,
                        SimpleProcessor processor,
                        SimpleWriter writer,
                        AsyncTaskExecutor taskExecutor) {

        return new StepBuilder("stepOne", jobRepository)
                .<String, String>chunk(5) // procesamos 5 elementos por transacción
                .reader((ItemReader<String>) reader)   // Reader exclusivo del Step
                .processor(processor)     // Processor exclusivo del Step
                .writer(writer)           // Writer exclusivo del Step
                .taskExecutor(taskExecutor) // paralelismo
                .faultTolerant()           // habilita tolerancia a errores
                .retryLimit(3)             // reintenta hasta 3 veces
                .retry(Exception.class)    // reintenta solo estas excepciones
                .skipLimit(5)              // permite saltar hasta 5 elementos fallidos
                .skip(Exception.class)     // ignora elementos con estas excepciones
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        System.out.println("[Job] Iniciando Step: " + stepExecution.getStepName());
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        System.out.println("[Job] Step finalizado: " + stepExecution.getStepName()
                                + ", Status: " + stepExecution.getStatus()
                                + ", Lecturas: " + stepExecution.getReadCount()
                                + ", Escritos: " + stepExecution.getWriteCount());
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }

    // =================== TASK EXECUTOR ===================
    /**
     * AsyncTaskExecutor que permite ejecutar chunks de un Step en paralelo.
     *
     * Configuración:
     * - CorePoolSize = 4: número mínimo de hilos
     * - MaxPoolSize = 4: número máximo de hilos
     * - ThreadNamePrefix = "batch-thread-": prefijo de los hilos para logs
     *
     * @return AsyncTaskExecutor configurado para Spring Batch
     */
    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("batch-thread-");
        executor.initialize();
        return executor;
    }

}
