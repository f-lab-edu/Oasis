package com.flab.oasis.model.exception;

import com.flab.oasis.constant.ErrorCode;
import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {
    private static final long serialVersionUID = -7138577059223481163L;

    private final ErrorCode errorCode;

    public AuthorizationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
