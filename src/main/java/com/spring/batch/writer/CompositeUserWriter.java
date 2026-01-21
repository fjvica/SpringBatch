package com.spring.batch.writer;

import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.stereotype.Component;

/**
 * Writer compuesto que escribe usuarios en múltiples destinos (archivo y DB)
 * usando Spring Batch 6, que requiere write(Chunk<? extends T>).
 */
@Component
public class CompositeUserWriter implements ItemWriter<User> {

    private final FlatFileItemWriter<User> fileWriter;
    private final JdbcBatchItemWriter<User> dbWriter;

    public CompositeUserWriter(FlatFileItemWriter<User> fileWriter,
                               JdbcBatchItemWriter<User> dbWriter) {
        this.fileWriter = fileWriter;
        this.dbWriter = dbWriter;
    }

    @Override
    public void write(Chunk<? extends User> chunk) throws Exception {
        // Pasamos directamente el Chunk a cada writer
        fileWriter.write(chunk); // ahora es correcto
        dbWriter.write(chunk);   // también correcto
    }
}
