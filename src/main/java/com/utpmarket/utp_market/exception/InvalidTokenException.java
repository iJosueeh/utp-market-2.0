package com.utpmarket.utp_market.exception;

/**
 * Excepción personalizada para tokens JWT inválidos o expirados
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
