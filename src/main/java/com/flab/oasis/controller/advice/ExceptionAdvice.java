package com.flab.oasis.controller.advice;

import com.flab.oasis.model.exception.AuthenticationException;
import com.flab.oasis.model.response.FailureResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<FailureResponse> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        FailureResponse.builder(e.getErrorCode().getCode())
                                .message(e.getMessage())
                                .build()
                );
    }
}
