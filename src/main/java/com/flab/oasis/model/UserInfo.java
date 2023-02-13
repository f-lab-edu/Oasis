package com.flab.oasis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfo {
    private String uid;
    private String nickName;
    private String introduce;
    private String avatarUrl;
}
