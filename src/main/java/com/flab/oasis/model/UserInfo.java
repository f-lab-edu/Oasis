package com.flab.oasis.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 5532404031944186139L;

    private String uid;
    private String nickname;
    private String introduce;
}
