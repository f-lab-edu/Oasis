package com.flab.oasis.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 4305118098455201936L;

    private String uid;
    private String password;
}
