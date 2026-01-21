package com.spring.batch.model;

import lombok.Getter;
import lombok.Setter;

/**
     * Modelo de usuario simple que usaremos para leer, procesar y escribir datos.
     */
    @Setter
    @Getter
    public class User {
    // Getters y setters
    private Long id;
        private String name;
        private String email;
        private boolean active;

        public User() {}

        public User(Long id, String name, String email, boolean active) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.active = active;
        }

    @Override
        public String toString() {
            return "User{id=%d, name='%s', email='%s', active=%s}"
                    .formatted(id, name, email, active);
        }
    }

