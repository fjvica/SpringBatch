package com.spring.batch.reader;

import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Reader compuesto que combina dos fuentes de datos:
 *  - Archivo CSV (usuarios externos)
 *  - Base de datos (usuarios activos)
 *
 * En Spring Batch 6 ya no existe CompositeItemReader.setDelegates(),
 * por lo que la lógica de combinación se implementa manualmente.
 */
@Configuration
public class CompositeUserReader {

    @Bean
    public ItemReader<User> compositeUserReader(
            FlatFileItemReader<User> csvReader,
            JdbcCursorItemReader<User> dbReader) {

        return new ItemReader<>() {
            private Iterator<User> iterator; // iterador interno de los usuarios combinados

            @Override
            public User read() throws Exception {
                // La primera vez que se llama, se cargan los datos desde ambas fuentes
                if (iterator == null) {
                    List<User> combined = new LinkedList<>();

                    // 1️⃣ Leer todos los usuarios desde el CSV
                    User csvUser;
                    while ((csvUser = csvReader.read()) != null) {
                        combined.add(csvUser);
                    }

                    // 2️⃣ Leer todos los usuarios desde la base de datos
                    User dbUser;
                    while ((dbUser = dbReader.read()) != null) {
                        combined.add(dbUser);
                    }

                    // 3️⃣ Crear iterador para recorrer ambos conjuntos como una sola fuente
                    iterator = combined.iterator();
                }

                // 4️⃣ Devolver un usuario en cada llamada, o null si se terminó
                return iterator.hasNext() ? iterator.next() : null;
            }
        };
    }
}


