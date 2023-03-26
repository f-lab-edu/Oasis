package com.flab.oasis.utils;

import com.flab.oasis.constant.JwtProperty;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;

public class CookieUtils {
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";

    public static String createCookie(String tokenType, String token) {
        int expireTime = tokenType.equals(ACCESS_TOKEN) ?
                JwtProperty.ACCESS_TOKEN_EXPIRE_TIME : JwtProperty.REFRESH_TOKEN_EXPIRE_TIME;

        return ResponseCookie.from(tokenType, token)
                .maxAge(expireTime / 1000)
                .httpOnly(true)
                .path("/")
                .sameSite(Cookie.SameSite.LAX.toString())
                .build()
                .toString();
    }
}
