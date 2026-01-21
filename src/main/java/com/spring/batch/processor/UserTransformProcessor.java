package com.spring.batch.processor;

import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Processor que transforma los datos del usuario.
 * En este caso, convierte el nombre a mayúsculas.
 */
@Component
public class UserTransformProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user) {
        user.setName(user.getName().trim().toUpperCase());
        return user;
    }
}

