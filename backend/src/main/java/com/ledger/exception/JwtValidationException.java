package com.ledger.exception;

/**
 * JWT验证异常
 */
public class JwtValidationException extends RuntimeException {

    private final String message;

    public JwtValidationException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
