package com.flab.oasis.model.exception;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.utils.LogUtils;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class FatalException extends RuntimeException {
    private static final long serialVersionUID = 6062766096885676942L;

    private final ErrorCode errorCode;

    public FatalException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;

        LogUtils.error(this.getClass(), errorCode, message);
    }

    public static String makeStackTraceMessage(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(throwable.getClass().getName()).append("\n");
        Arrays.stream(throwable.getStackTrace())
                .forEach(e -> stringBuilder.append(e.toString()).append("\n"));

        return stringBuilder.toString();
    }
}
