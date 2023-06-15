package com.flab.oasis.model.exception;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.utils.LogUtils;
import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {
    private static final long serialVersionUID = -7138577059223481163L;

    private final ErrorCode errorCode;
    private String value;

    public AuthenticationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;

        LogUtils.error(this.getClass(), errorCode, message);
    }

    public AuthenticationException(ErrorCode errorCode, String message, String value) {
        super(message);
        this.errorCode = errorCode;
        this.value = value;

        LogUtils.error(this.getClass(), errorCode, message, value);
    }
}
