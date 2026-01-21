package com.spring.batch.writer;

import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

/**
 * Writer que escribe los usuarios procesados en un archivo de texto plano.
 *
 * <p>Este Writer genera un archivo con una línea por usuario procesado.
 * Cada línea utiliza el metodo {@code toString()} del modelo {@link User}.
 */
@Configuration
public class FileUserWriter {

    /**
     * Crea un FlatFileItemWriter configurado para escribir objetos User en un archivo local.
     *
     * @return FlatFileItemWriter<User> configurado con recurso, codificación y agregador de líneas
     */
    @Bean
    public FlatFileItemWriter<User> fileUserWriter() {
        return new FlatFileItemWriterBuilder<User>()
                .name("fileUserWriter") // nombre interno del writer
                .resource(new FileSystemResource("output/processed_users.txt")) // archivo destino
                .lineAggregator(User::toString) // cómo convertir cada objeto en línea
                .encoding("UTF-8") // opcional, pero recomendable
                .build();
    }
}


