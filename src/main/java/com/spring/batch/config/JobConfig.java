package com.spring.batch.config;

import com.spring.batch.processor.SimpleProcessor;
import com.spring.batch.reader.SimpleReader;
import com.spring.batch.writer.SimpleWriter;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Job y Step usando la API moderna de Spring Batch 6.x
 *
 * En Batch 6:
 * - No existen JobBuilderFactory ni StepBuilderFactory
 * - Se construyen Jobs y Steps directamente usando JobRepository
 * - Los chunks ya no requieren TransactionManager explícito
 *
 * Patrón típico:
 * Job -> Step -> ItemReader -> ItemProcessor -> ItemWriter
 */
@Configuration
public class JobConfig {

    /**
     * Job principal llamado "exampleJob".
     * Contiene un único Step "exampleStep"
     */
    @Bean
    public Job exampleJob(JobRepository jobRepository, Step exampleStep) {
        return new JobBuilder("exampleJob", jobRepository)
                .start(exampleStep) // define el primer step del job
                .build();           // construye el job completo
    }

    /**
     * Step principal llamado "exampleStep".
     * - chunk(1): procesa 1 elemento por transacción
     * - reader: lee datos de entrada
     * - processor: transforma o valida datos
     * - writer: escribe datos procesados
     */
    @Bean
    public Step exampleStep(JobRepository jobRepository,
                            SimpleReader reader,
                            SimpleProcessor processor,
                            SimpleWriter writer) {
        return new StepBuilder("exampleStep", jobRepository)
                .<String, String>chunk(1) // chunk moderno: tamaño de lote de 1
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}




