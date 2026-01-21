package com.spring.batch.reader;

import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Reader que lee usuarios desde un archivo CSV utilizando FlatFileItemReaderBuilder (Spring Batch 6).
 *
 * Ejemplo de archivo (resources/data/users.csv):
 * id,name,email,active
 * 1,John,john@mail.com,true
 * 2,Ana,ana@mail.com,false
 */
@Configuration
public class CsvUserReader {

    /**
     * Configura un lector de archivos CSV para objetos User.
     *
     * @return un FlatFileItemReader<User> configurado con mapeo de campos.
     */
    @Bean
    public FlatFileItemReader<User> flatFileUserReader() {
        // Tokenizer: define cómo separar las columnas del CSV
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("id", "name", "email", "active");

        // Mapeador: convierte los valores del CSV en una instancia de User
        BeanWrapperFieldSetMapper<User> fieldMapper = new BeanWrapperFieldSetMapper<>();
        fieldMapper.setTargetType(User.class);

        // LineMapper: une el tokenizer y el fieldMapper
        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldMapper);

        // Builder moderno de Spring Batch 6
        return new FlatFileItemReaderBuilder<User>()
                .name("flatFileUserReader")                // identificador del reader
                .resource(new ClassPathResource("data/users.csv")) // ubicación del CSV
                .linesToSkip(1)                            // omitir cabecera
                .lineMapper(lineMapper)                    // cómo mapear cada línea
                .build();
    }
}
