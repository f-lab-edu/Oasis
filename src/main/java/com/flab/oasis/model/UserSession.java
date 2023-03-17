package com.flab.oasis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class UserSession implements Serializable {
    private static final long serialVersionUID = -5705851952729691741L;

    private String uid;
    private String refreshToken;
}
