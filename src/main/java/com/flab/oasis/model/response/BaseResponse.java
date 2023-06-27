package com.flab.oasis.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public abstract class BaseResponse implements Serializable {
    private static final long serialVersionUID = -7447317671473404857L;

    private int code;
    private String message;
}
