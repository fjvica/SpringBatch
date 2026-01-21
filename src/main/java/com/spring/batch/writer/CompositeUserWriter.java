package com.spring.batch.writer;

import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Writer que combina múltiples destinos de salida.
 * En este ejemplo:
 *  - Archivo de texto
 *  - Base de datos
 */
@Configuration
public class CompositeUserWriter {

    @Bean
    public CompositeItemWriter<User> compositeUserWriter(
            FlatFileItemWriter<User> fileWriter,
            JdbcBatchItemWriter<User> dbWriter) {
        CompositeItemWriter<User> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(fileWriter, dbWriter));
        return writer;
    }
}
