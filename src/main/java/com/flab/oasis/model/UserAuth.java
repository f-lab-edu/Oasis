package com.flab.oasis.model;

import com.flab.oasis.constant.UserRole;
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
    private UserRole userRole;
}
