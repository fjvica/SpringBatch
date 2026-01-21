package com.spring.batch.reader;

import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Reader compuesto que combina dos fuentes de datos:
 *  - Archivo CSV (usuarios externos)
 *  - Base de datos (usuarios activos)
 *
 * En Spring Batch 6 ya no existe CompositeItemReader.setDelegates(),
 * por lo que la lógica de combinación se implementa manualmente.
 */
@Component
public class CompositeUserReader implements ItemReader<User> {

    private final FlatFileItemReader<User> csvReader;
    private final JdbcCursorItemReader<User> dbReader;

    private Iterator<User> iterator; // iterador interno de los usuarios combinados

    public CompositeUserReader(FlatFileItemReader<User> csvReader,
                               JdbcCursorItemReader<User> dbReader) {
        this.csvReader = csvReader;
        this.dbReader = dbReader;
    }

    @Override
    public User read() throws Exception {
        // Inicializamos iterator si es null
        if (iterator == null) {
            List<User> combined = new ArrayList<>();

            // Leemos todos los usuarios del CSV
            User item;
            csvReader.open(null); // ExecutionContext nulo en este ejemplo
            while ((item = csvReader.read()) != null) {
                combined.add(item);
            }
            csvReader.close();

            // Leemos todos los usuarios de la BD
            dbReader.open(null);
            while ((item = dbReader.read()) != null) {
                combined.add(item);
            }
            dbReader.close();

            // Creamos iterador
            iterator = combined.iterator();
        }

        // Devolvemos siguiente elemento o null si no hay más
        return iterator.hasNext() ? iterator.next() : null;
    }
}
