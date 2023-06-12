package com.flab.oasis.model.exception;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.utils.LogUtils;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private static final long serialVersionUID = 9105317195181984181L;

    private final ErrorCode errorCode;

    public NotFoundException(ErrorCode errorCode, String message, String value) {
        super(message);
        this.errorCode = errorCode;

        LogUtils.error(this.getClass(), errorCode, message, value);
    }
}
