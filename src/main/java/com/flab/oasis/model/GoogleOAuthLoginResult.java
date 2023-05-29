package com.flab.oasis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleOAuthLoginResult {
    JwtToken jwtToken;
    String uid;
    boolean joinState;
}
