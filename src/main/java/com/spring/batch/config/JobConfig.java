package com.spring.batch.config;

import com.spring.batch.model.User;
import com.spring.batch.processor.ActiveUserFilterProcessor;
import com.spring.batch.processor.UserTransformProcessor;
import com.spring.batch.processor.UserValidationProcessor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuración avanzada de Spring Batch 6 para un Job con un único Step.
 *
 * <p>Este Job implementa un flujo ETL completo (Extract–Transform–Load):</p>
 * <ul>
 *     <li><b>Extract:</b> se combinan datos de múltiples fuentes
 *         mediante un {@link ItemReader} personalizado (CompositeUserReader)
 *         que une CSV y base de datos.</li>
 *     <li><b>Transform:</b> se aplican tres procesadores en secuencia:
 *         validación, transformación y filtrado.</li>
 *     <li><b>Load:</b> los datos se escriben en varios destinos
 *         mediante un {@link ItemWriter} compuesto (CompositeUserWriter)
 *         que escribe simultáneamente en archivo y base de datos.</li>
 * </ul>
 *
 * <p>El Step usa procesamiento por chunks, ejecución paralela con AsyncTaskExecutor
 * y tolerancia a errores con retry/skip.</p>
 */
@Configuration
public class JobConfig {

    /** Repositorio de metadatos de Spring Batch (almacena estado de Jobs y Steps). */
    private final JobRepository jobRepository;

    /**
     * Constructor que inyecta el {@link JobRepository}.
     *
     * @param jobRepository repositorio de Spring Batch
     */
    public JobConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // ======================= JOB =======================

    /**
     * Define el Job principal llamado {@code jobOne}.
     *
     * <p>Este Job ejecuta un único Step (stepOne), y su estado se guarda en las
     * tablas internas de Spring Batch, permitiendo reinicios automáticos o
     * seguimiento de ejecución.</p>
     *
     * @param stepOne Step principal del Job
     * @return instancia configurada del Job
     */
    @Bean
    public Job jobOne(Step stepOne) {
        return new JobBuilder("jobOne", jobRepository)
                .start(stepOne)  // único Step del Job
                .build();
    }

    // ======================= STEP =======================

    /**
     * Step principal del Job.
     *
     * <p>Características clave:</p>
     * <ul>
     *     <li>Procesamiento por chunks de 5 elementos.</li>
     *     <li>Lectura de datos combinados desde varias fuentes (CSV + BD).</li>
     *     <li>Aplicación de validación, transformación y filtrado.</li>
     *     <li>Escritura simultánea en archivo y base de datos.</li>
     *     <li>Paralelismo con AsyncTaskExecutor (4 hilos).</li>
     *     <li>Tolerancia a fallos con reintentos y omisión de registros erróneos.</li>
     * </ul>
     *
     * @param reader       Reader que extrae los datos de origen (bean {@code compositeUserReader})
     * @param validator    Processor que valida los datos de entrada
     * @param transformer  Processor que transforma campos del usuario
     * @param filter       Processor que descarta usuarios inactivos
     * @param writer       Writer que persiste datos en múltiples destinos (bean {@code compositeUserWriter})
     * @param taskExecutor Executor que permite paralelismo en el procesamiento de chunks
     * @return instancia configurada del Step
     */
    @Bean
    public Step stepOne(ItemReader<User> reader,
                        UserValidationProcessor validator,
                        UserTransformProcessor transformer,
                        ActiveUserFilterProcessor filter,
                        ItemWriter<User> writer,
                        AsyncTaskExecutor taskExecutor) {

        return new StepBuilder("stepOne", jobRepository)
                // Procesa 5 elementos por transacción
                .<User, User>chunk(5)
                // Fuente de datos combinada (CSV + BD)
                .reader(reader)
                // Encadenamiento de procesadores manualmente
                .processor(item -> {
                    User u = validator.process(item);     // valida formato de email
                    u = transformer.process(u);           // transforma nombre, limpia datos
                    return filter.process(u);             // filtra usuarios inactivos
                })
                // Escritura combinada en archivo y BD
                .writer(writer)
                // Ejecución paralela de chunks
                .taskExecutor(taskExecutor)
                // Tolerancia a fallos
                .faultTolerant()
                .retryLimit(3)   // reintenta hasta 3 veces si ocurre una Exception
                .skipLimit(5)    // permite omitir hasta 5 registros defectuosos
                .build();
    }

    // =================== TASK EXECUTOR ===================

    /**
     * Configura un {@link AsyncTaskExecutor} que permite ejecutar varios chunks en paralelo.
     *
     * <p>Parámetros:</p>
     * <ul>
     *     <li><b>CorePoolSize = 4:</b> número mínimo de hilos activos.</li>
     *     <li><b>MaxPoolSize = 4:</b> número máximo de hilos simultáneos.</li>
     *     <li><b>ThreadNamePrefix = "batch-thread-":</b> prefijo para identificar hilos en logs.</li>
     * </ul>
     *
     * @return ejecutor asíncrono configurado para uso con Spring Batch
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
