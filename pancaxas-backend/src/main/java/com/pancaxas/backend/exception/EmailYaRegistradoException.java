package com.pancaxas.backend.exception;

public class EmailYaRegistradoException extends RuntimeException {
    public EmailYaRegistradoException(String correo) {
        super("Ya existe una cuenta registrada con el correo: " + correo);
    }
}
