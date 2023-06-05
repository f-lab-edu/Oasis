package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GoogleOAuthLoginRequest implements Serializable {
    private static final long serialVersionUID = -3778175011718972617L;

    private String token;
}
