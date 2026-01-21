package com.spring.batch.writer;

import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Writer que inserta usuarios procesados en la base de datos.
 */
@Configuration
public class DbUserWriter {

    @Bean
    public JdbcBatchItemWriter<User> dbInsertUserWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("INSERT INTO processed_users (id, name, email, active) VALUES (:id, :name, :email, :active)")
                .beanMapped() // usa getters del bean User
                .build();
    }
}

