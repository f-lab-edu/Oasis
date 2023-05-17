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

    public static void error(String exceptionClassName, ErrorCode errorCode, String message) {
        LOGGER.error(
                "ExceptionClass: {}\nErrorCode: {}({})\nMessage: {}",
                exceptionClassName, errorCode, errorCode.getCode(), message
        );
    }

    public static void error(String exceptionClassName, ErrorCode errorCode, String message, String value) {
        LOGGER.error(
                "ExceptionClass: {}\nErrorCode: {}({})\nMessage: {}\nValue: {}",
                exceptionClassName, errorCode, errorCode.getCode(), message, value
        );
    }
}