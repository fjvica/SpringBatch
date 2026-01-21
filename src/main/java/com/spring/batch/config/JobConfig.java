package com.spring.batch.config;

import com.spring.batch.model.User;
import com.spring.batch.processor.ActiveUserFilterProcessor;
import com.spring.batch.processor.UserTransformProcessor;
import com.spring.batch.processor.UserValidationProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración avanzada de Spring Batch 6 para un Job completo con:
 * - Steps condicionales
 * - Flujo paralelo
 * - JobParameters
 * - Retry y Skip avanzados
 * - Listeners detallados
 */
@Configuration
public class JobConfig {

    private final JobRepository jobRepository;

    public JobConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // ======================= JOB PRINCIPAL =======================

    @Bean
    public Job advancedJob(Step stepOne, Step stepTwo, Step stepThree) {

        // Flujo paralelo
        Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(stepTwo)
                .next(stepThree)
                .build();

        // Job con Step condicional y flujo paralelo
        return new JobBuilder("advancedJob", jobRepository)
                .start(stepOne)
                .on("FAILED").fail()       // Si falla stepOne, Job termina
                .on("*").to(parallelFlow)  // Si completa, se ejecuta flujo paralelo
                .end()
                .build();
    }

    // ======================= STEP ONE (ETL) =======================

    @Bean
    public Step stepOne(ItemReader<User> reader,
                        UserValidationProcessor validator,
                        UserTransformProcessor transformer,
                        ActiveUserFilterProcessor filter,
                        ItemWriter<User> writer,
                        AsyncTaskExecutor taskExecutor,
                        LoggingListeners loggingListeners,
                        SkipPolicy customSkipPolicy) {

        return new StepBuilder("stepOne", jobRepository)
                .<User, User>chunk(5)
                .reader(reader)
                .processor(item -> {
                    User u = validator.process(item);
                    u = transformer.process(u);
                    return filter.process(u);
                })
                .writer(writer)
                .taskExecutor(taskExecutor)
                .faultTolerant()
                .skipPolicy(customSkipPolicy)
                .retry(IllegalArgumentException.class) // tipo de excepción a reintentar
                .retryLimit(3)                          // máximo de reintentos
                .listener(loggingListeners)
                .build();
    }

    // ======================= STEP DOS Y TRES (PARALELO) =======================

    @Bean
    public Step stepTwo(ItemReader<User> reader, ItemWriter<User> writer) {
        return new StepBuilder("stepTwo", jobRepository)
                .<User, User>chunk(5)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Step stepThree(ItemReader<User> reader, ItemWriter<User> writer) {
        return new StepBuilder("stepThree", jobRepository)
                .<User, User>chunk(5)
                .reader(reader)
                .writer(writer)
                .build();
    }

    // =================== TASK EXECUTOR ===================

    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("batch-thread-");
        executor.initialize();
        return executor;
    }

    // =================== SKIP POLICY PERSONALIZADA ===================

    @Bean
    public SkipPolicy customSkipPolicy() {
        return (throwable, skipCount) -> {
            // Ignora hasta 5 errores de parseo de archivos
            return throwable instanceof Exception && skipCount < 5;
        };
    }

    // =================== LISTENERS ===================

    @Bean
    public LoggingListeners loggingListeners() {
        return new LoggingListeners();
    }
}
