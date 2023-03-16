package com.flab.oasis.model.dao;

import com.flab.oasis.model.UserSession;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserAuth {
    private String uid;
    private String password;
    private String salt;
    private char socialYN;
    private String refreshToken;

    public boolean comparePassword(String password) {
        return this.password.equals(password);
    }

    public UserSession parseUserSession() {
        return UserSession.builder()
                .uid(uid)
                .refreshToken(refreshToken)
                .build();
    }
}
