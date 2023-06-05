package com.flab.oasis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseReissuedJWT {
    private int code;
    private String message;
    private JsonWebToken reissuedJWT;
}