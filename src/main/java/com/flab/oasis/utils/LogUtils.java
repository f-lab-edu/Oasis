package com.flab.oasis.utils;

import com.flab.oasis.constant.ErrorCode;

public class LogUtils {
    public static String makeLog(String message) {
        return String.format("Message: %s", message);
    }

    public static String makeLog(String message, String value) {
        return String.format("Message: %s%nValue: %s", message, value);
    }

    public static String makeErrorLog(ErrorCode errorCode, String message) {
        return String.format(
                "ErrorCode: %s(%d)%nMessage: %s",
                errorCode, errorCode.getCode(), message
        );
    }

    public static String makeErrorLog(ErrorCode errorCode, String message, String value) {
        return String.format(
                "ErrorCode: %s(%d)%nMessage: %s%nValue: %s",
                errorCode, errorCode.getCode(), message, value
        );
    }
}