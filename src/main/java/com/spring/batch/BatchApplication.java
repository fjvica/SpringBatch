package com.spring.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de Spring Boot.
 *
 * @SpringBootApplication combina:
 * - @Configuration: permite definir beans
 * - @EnableAutoConfiguration: auto-configura Spring Boot y Batch
 * - @ComponentScan: detecta @Component, @Service, etc.
 *
 * Al ejecutar esta clase, Spring Boot inicia:
 * - El contexto de Spring
 * - Todos los beans definidos
 * - Spring Batch: ejecuta jobs si están configurados
 */
@SpringBootApplication
public class BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

}
