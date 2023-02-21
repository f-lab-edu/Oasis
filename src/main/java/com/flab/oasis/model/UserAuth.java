package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAuth {
    private String uid;
    private String password;
    private String salt;
    private char socialYN;
    private String accessToken;
    private String refreshToken;
}
