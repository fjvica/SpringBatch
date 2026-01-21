package com.spring.batch.config;

import com.spring.batch.model.User;
import org.springframework.batch.core.listener.ItemProcessListener;
import org.springframework.batch.core.listener.ItemReadListener;
import org.springframework.batch.core.listener.ItemWriteListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class LoggingListeners implements ItemReadListener<User>, ItemProcessListener<User, User>, ItemWriteListener<User> {

    @Override
    public void beforeRead() { System.out.println("[Read] Antes de leer un item"); }
    @Override
    public void afterRead(User item) { System.out.println("[Read] Item leído: " + item); }
    @Override
    public void onReadError(Exception ex) { System.out.println("[Read] Error leyendo: " + ex.getMessage()); }

    @Override
    public void beforeProcess(User item) { System.out.println("[Process] Antes de procesar: " + item); }
    @Override
    public void afterProcess(User item, User result) { System.out.println("[Process] Procesado: " + result); }
    @Override
    public void onProcessError(User item, Exception ex) { System.out.println("[Process] Error procesando: " + item); }

    @Override
    public void beforeWrite(Chunk<? extends User> chunk) {
        System.out.println("[Write] Antes de escribir " + chunk.getItems().size() + " items");
    }
    @Override
    public void afterWrite(Chunk<? extends User> chunk) {
        System.out.println("[Write] Escritos " + chunk.getItems().size() + " items");
    }
    @Override
    public void onWriteError(Exception ex, Chunk<? extends User> chunk) {
        System.out.println("[Write] Error escribiendo: " + ex.getMessage());
    }
}

