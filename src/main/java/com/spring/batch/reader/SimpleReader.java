package com.spring.batch.reader;

import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SimpleReader: lee datos desde un archivo CSV.
 *
 * En Batch 6, ItemReader sigue devolviendo T por read().
 * Cada llamada a read() devuelve un solo elemento.
 *
 * Funcionamiento:
 * 1. Carga todas las líneas del archivo CSV en memoria
 * 2. Mantiene un índice currentIndex
 * 3. Devuelve una línea por read()
 * 4. Retorna null al final indicando que no hay más elementos
 */
public class SimpleReader implements ItemReader<String> {

    private final List<String> lines;
    private int currentIndex = 0;

    public SimpleReader() throws Exception {
        // Cargar archivo CSV desde recursos
        var resource = new ClassPathResource("data/input.csv");
        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            lines = reader.lines().collect(Collectors.toList());
        }
    }

    @Override
    public String read() {
        if (currentIndex < lines.size()) {
            return lines.get(currentIndex++);
        }
        return null; // fin de archivo, el Step sabe que terminó
    }
}


