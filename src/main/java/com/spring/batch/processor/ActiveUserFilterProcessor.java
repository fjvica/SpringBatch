package com.spring.batch.processor;

import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Processor que filtra usuarios inactivos.
 * Si el usuario no está activo, devuelve null (Spring Batch lo ignora).
 */
@Component
public class ActiveUserFilterProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user) {
        return user.isActive() ? user : null;
    }
}

