package com.flab.oasis.model.response;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.model.exception.UnexpectedCodeException;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder(builderMethodName = "hiddenBuilder")
public class FailureResponse extends BaseResponse {
    private static final long serialVersionUID = 7291705160666614470L;

    public static FailureResponseBuilder<?, ?> builder(int code) {
        if (code == 200) {
            throw new UnexpectedCodeException(
                    ErrorCode.EXPECTATION_FAILED, "Unexpected Error Code.", String.valueOf(code)
            );
        }

        return hiddenBuilder().code(code);
    }
}
