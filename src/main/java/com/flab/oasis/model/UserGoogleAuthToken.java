package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserGoogleAuthToken implements Serializable {
    private static final long serialVersionUID = 4327253127271475375L;
    private String token;
}
