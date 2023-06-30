package com.flab.oasis.model.response;

import com.flab.oasis.model.JsonWebToken;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class JsonWebTokenResponse extends BaseResponse {
    private static final long serialVersionUID = -4159196522419234339L;

    private JsonWebToken jsonWebToken;
}
