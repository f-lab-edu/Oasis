package com.flab.oasis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class JwtToken implements Serializable {
    private static final long serialVersionUID = 3859480374174704226L;

    private String accessToken;
    private String refreshToken;
}
