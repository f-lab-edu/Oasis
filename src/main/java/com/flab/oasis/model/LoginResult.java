package com.flab.oasis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class LoginResult implements Serializable {
    private static final long serialVersionUID = -4259470355854666725L;

    private JsonWebToken jsonWebToken;
    private boolean joinUser;
}
