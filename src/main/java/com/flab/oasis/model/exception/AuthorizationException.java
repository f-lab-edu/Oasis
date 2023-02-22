package com.flab.oasis.model.exception;

public class AuthorizationException extends RuntimeException {
    private static final long serialVersionUID = -7138577059223481163L;

    public AuthorizationException(String message) {
        super(message);
    }
}
