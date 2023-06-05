package com.flab.oasis.utils;

import com.flab.oasis.constant.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);

    public static void info(String message) {
        LOGGER.info("Message: {}", message);
    }

    public static void info(String message, String value) {
        LOGGER.info("Message: {}\nValue: {}", message, value);
    }

    public static void error(Class<?> exception, ErrorCode errorCode, String message) {
        LOGGER.error(
                "Class: {}\nErrorCode: {}({})\nMessage: {}",
                exception.getName(), errorCode, errorCode.getCode(), message
        );
    }

    public static void error(Class<?> exception, ErrorCode errorCode, String message, String value) {
        LOGGER.error(
                "Class: {}\nErrorCode: {}({})\nMessage: {}\nValue: {}",
                exception.getName(), errorCode, errorCode.getCode(), message, value
        );
    }
}