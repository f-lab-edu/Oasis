package com.flab.oasis.model.response;

import com.flab.oasis.constant.ResponseCode;

import java.io.Serializable;

public class SuccessResponse extends BaseResponse {
    private static final long serialVersionUID = -291461128620563480L;

    public SuccessResponse() {
        super(ResponseCode.OK.getCode(), "success");
    }
}
