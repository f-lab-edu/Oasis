package com.flab.oasis.controller.advice;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.model.GeneralResponse;
import com.flab.oasis.model.exception.AuthenticationException;
import com.flab.oasis.utils.LogUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GeneralResponse<String>> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        GeneralResponse.<String>builder()
                                .code(e.getErrorCode().getCode())
                                .message(e.getMessage())
                                .data(e.getValue())
                                .build()
                );
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<GeneralResponse<String>> handleSQLException(SQLException e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        LogUtils.error(e.getClass(), errorCode, e.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        GeneralResponse.<String>builder()
                                .code(errorCode.getCode())
                                .message(e.getMessage())
                                .build()
                );
    }
}
