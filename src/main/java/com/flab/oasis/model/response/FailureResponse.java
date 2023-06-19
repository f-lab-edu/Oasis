package com.flab.oasis.model.response;

import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
public class FailureResponse extends BaseResponse implements Serializable {
    private static final long serialVersionUID = 7291705160666614470L;
}
