package com.flab.oasis.controller.advice;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.model.exception.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ResponseError> handleAuthorizationException(AuthorizationException e) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseError(e.getErrorCode().getCode(), e.getMessage()));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ResponseError> handleSQLException(SQLException e) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseError(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage()));
    }
}
