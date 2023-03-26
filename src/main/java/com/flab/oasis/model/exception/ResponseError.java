package com.flab.oasis.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ResponseError implements Serializable {
    private static final long serialVersionUID = 1772002427691231525L;

    private int code;
    private String message;
}
