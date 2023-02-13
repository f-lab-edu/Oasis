package com.flab.oasis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAuth {
    private String uid;
    private String password;
    private char socialYN;
    private String accessToken;
    private String refreshToken;
}
