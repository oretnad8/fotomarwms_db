package com.fotomar.mensajesservice.exception;

public class MensajeNotFoundException extends RuntimeException {
    public MensajeNotFoundException(String message) {
        super(message);
    }
}