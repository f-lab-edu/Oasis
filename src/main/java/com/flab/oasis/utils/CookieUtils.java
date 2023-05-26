package com.flab.oasis.utils;

import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.model.JwtToken;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.HttpServletResponse;

public class CookieUtils {
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";

    public static String createCookie(String tokenType, String token) {
        return ResponseCookie.from(tokenType, token)
                .maxAge(JwtProperty.REFRESH_TOKEN_EXPIRE_TIME / 1000)
                .httpOnly(true)
                .path("/")
                .sameSite(Cookie.SameSite.LAX.toString())
                .build()
                .toString();
    }

    public static void setCookieHeader(HttpServletResponse response, JwtToken jwtToken) {
        response.addHeader(
                SET_COOKIE,
                CookieUtils.createCookie(ACCESS_TOKEN, jwtToken.getAccessToken())
        );
        response.addHeader(
                SET_COOKIE,
                CookieUtils.createCookie(REFRESH_TOKEN, jwtToken.getRefreshToken())
        );
    }
}
