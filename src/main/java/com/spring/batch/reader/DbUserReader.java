package com.spring.batch.reader;


import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Reader que obtiene usuarios activos desde una base de datos.
 * Ejemplo de tabla: users(id, name, email, active)
 */
@Configuration
public class DbUserReader {

    @Bean
    public JdbcCursorItemReader<User> jdbcUserReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<User>()
                .name("jdbcUserReader")
                .dataSource(dataSource)
                .sql("SELECT id, name, email, active FROM users WHERE active = true")
                .rowMapper((rs, rowNum) -> new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getBoolean("active")
                ))
                .build();
    }
}

