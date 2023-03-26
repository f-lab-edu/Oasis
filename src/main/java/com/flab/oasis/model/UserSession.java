package com.flab.oasis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSession implements Serializable {
    private static final long serialVersionUID = -5705851952729691741L;

    private String uid;
    private String refreshToken;
}
