package com.flab.oasis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JsonWebToken {
    private String accessToken;
    private String refreshToken;
}
