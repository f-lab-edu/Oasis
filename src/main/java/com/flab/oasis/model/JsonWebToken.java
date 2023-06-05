package com.flab.oasis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class JsonWebToken implements Serializable {
    private static final long serialVersionUID = 369698728585910676L;

    private String accessToken;
    private String refreshToken;
}
