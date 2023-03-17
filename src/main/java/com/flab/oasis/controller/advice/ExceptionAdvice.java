package com.flab.oasis.controller.advice;

import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.model.exception.ResponseError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ResponseError> handleAuthorizationException(AuthorizationException e) {
        return ResponseEntity.status(e.getErrorCode().getCode())
                .body(new ResponseError(e.getMessage()));
    }
}
