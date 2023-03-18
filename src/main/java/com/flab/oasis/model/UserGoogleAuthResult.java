package com.flab.oasis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserGoogleAuthResult {
    private String uid;
    private JwtToken jwtToken;
}
