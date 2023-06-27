package com.flab.oasis.model.exception;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.utils.LogUtils;
import lombok.Getter;

@Getter
public class UnexpectedCodeException extends RuntimeException {
    private static final long serialVersionUID = 5951607318840193660L;

    private final ErrorCode errorCode;

    public UnexpectedCodeException(ErrorCode errorCode, String message, String value) {
        super(message);
        this.errorCode = errorCode;

        LogUtils.error(this.getClass(), errorCode, message, value);
    }
}
