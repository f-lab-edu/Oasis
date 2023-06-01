package com.flab.oasis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResult {
    String uid;
    JsonWebToken jsonWebToken;
    boolean joinUser;
}
