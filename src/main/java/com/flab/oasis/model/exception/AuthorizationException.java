package com.flab.oasis.model.exception;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.utils.LogUtils;
import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {
    private static final long serialVersionUID = -7138577059223481163L;

    private final ErrorCode errorCode;

    public AuthorizationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;

        LogUtils.error(errorCode, message);
    }

    public AuthorizationException(ErrorCode errorCode, String message, String value) {
        super(message);
        this.errorCode = errorCode;

        LogUtils.error(errorCode, message, value);
    }
}
