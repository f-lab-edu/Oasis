package com.flab.oasis.controller.advice;

import com.flab.oasis.model.GeneralResponse;
import com.flab.oasis.model.exception.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
