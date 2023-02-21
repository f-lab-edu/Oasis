package com.flab.oasis.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest implements BaseRequest {
    private String uid;
    private String password;

    @Override
    public String generateEhCacheKey() {
        return uid;
    }
}
