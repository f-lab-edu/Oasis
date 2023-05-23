package com.flab.oasis.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuth implements Serializable {
    private static final long serialVersionUID = 4199401124507626L;

    private String uid;
    private String password;
    private String salt;
    private char socialYN;
    private String refreshToken;
}
