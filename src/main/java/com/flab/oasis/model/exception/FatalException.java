package com.flab.oasis.model.exception;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.utils.LogUtils;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class FatalException extends RuntimeException {
    private static final long serialVersionUID = 6062766096885676942L;

    private final ErrorCode errorCode;

    public FatalException(Throwable throwable, ErrorCode errorCode) {
        super(throwable.getMessage());
        this.errorCode = errorCode;

        LogUtils.error(throwable.getClass(), errorCode, parseStackTraceToString(throwable.getStackTrace()));
    }

    private String parseStackTraceToString(StackTraceElement[] stackTraceElements) {
        StringBuilder stringBuilder = new StringBuilder();

        Arrays.stream(stackTraceElements)
                .forEach(e -> stringBuilder.append(e.toString()).append("\n"));

        return stringBuilder.toString();
    }
}
