package com.flab.oasis.constant;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("ROLE_USER");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }
}
