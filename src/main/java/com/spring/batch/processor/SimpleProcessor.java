package com.spring.batch.processor;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * SimpleProcessor: transforma los datos leídos.
 *
 * En Batch 6, ItemProcessor<T, R> sigue igual:
 * - T: tipo de entrada (del Reader)
 * - R: tipo de salida (al Writer)
 *
 * En este ejemplo, convierte cada línea a mayúsculas.
 * Se pueden añadir validaciones, filtros o transformaciones más complejas.
 */
@Component
public class SimpleProcessor implements ItemProcessor<String, String> {

    @Override
    public String process(String item) {
        // Transformación simple: convertir a mayúsculas
        return item.toUpperCase();
    }
}


