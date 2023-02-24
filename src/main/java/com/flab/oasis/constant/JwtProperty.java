package com.flab.oasis.constant;

// 추후 NCP의 Object Storage로 분리 예정
public class JwtProperty {
    public static final String SECRET_KEY = "OasisAuth";
    public static final String ISSUER = "OasisServer";
    public static final int ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; // 1일
    public static final int REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 1주일
    public static final int REFRESH_TOKEN_REISSUE_TIME = 1000 * 60 * 60 * 24; // 1일
}
