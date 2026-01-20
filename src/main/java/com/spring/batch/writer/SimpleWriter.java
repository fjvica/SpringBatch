package com.spring.batch.writer;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * SimpleWriter: escribe los datos procesados.
 *
 * En Batch 6.x, ItemWriter recibe un Chunk<? extends T> en lugar de List<T>.
 * Un Chunk contiene todos los elementos procesados en la transacción actual.
 *
 * Funcionalidad:
 * - Itera sobre chunk.getItems()
 * - Imprime cada elemento en consola
 */
@Component
public class SimpleWriter implements ItemWriter<String> {

    @Override
    public void write(Chunk<? extends String> chunk) {
        for (String item : chunk.getItems()) {
            System.out.println(item);
        }
    }
}



