package com.flab.oasis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest implements BaseRequest, Serializable {
    private static final long serialVersionUID = 4305118098455201936L;

    private String uid;
    private String password;

    @Override
    public String generateEhCacheKey() {
        return uid;
    }
}
