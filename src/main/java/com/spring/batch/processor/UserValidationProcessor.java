package com.spring.batch.processor;


import com.spring.batch.model.User;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Processor que valida el formato del email.
 * Si el email no contiene '@', lanza una excepción para ser manejada por retry/skip.
 */
@Component
public class UserValidationProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user) {
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email inválido: " + user.getEmail());
        }
        return user;
    }
}

