package com.educa.msprestamos.exception;

public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}