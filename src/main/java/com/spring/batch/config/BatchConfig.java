package com.spring.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración base de Spring Batch 6.x
 *
 * @EnableBatchProcessing habilita:
 * - JobRepository: almacena el estado de jobs y steps
 * - JobLauncher: permite iniciar jobs
 * - JobRegistry: registra todos los jobs disponibles
 * - PlatformTransactionManager: gestiona transacciones de los steps
 *
 * En Batch 6.x, los builders modernos usan JobRepository y TransactionManager
 * que Spring Boot inyecta automáticamente.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    // Por ahora no se necesitan más beans, funciona con la configuración automática de Spring Boot
}



