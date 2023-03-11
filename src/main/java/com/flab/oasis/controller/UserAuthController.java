package com.flab.oasis.controller;

import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserGoogleAuthToken;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final JwtService jwtService;

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String ACCESS_TOKEN = "AccessToken";
    private static final String REFRESH_TOKEN = "RefreshToken";

    @PostMapping("/refresh")
    public boolean reissueJwtToken(@CookieValue("RefreshToken") String refreshToken, HttpServletResponse response) {
        JwtToken jwtToken = jwtService.reissueJwtToken(refreshToken);

        response.setHeader(SET_COOKIE, createCookie(ACCESS_TOKEN, jwtToken.getAccessToken()));
        response.setHeader(SET_COOKIE, createCookie(REFRESH_TOKEN, jwtToken.getRefreshToken()));

        return true;
    }

    @PostMapping("/login/default")
    public boolean loginAuthFromUserLoginRequest(
            @RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        JwtToken jwtToken = userAuthService.createJwtTokenByUserLoginRequest(userLoginRequest);

        response.setHeader(SET_COOKIE, createCookie(ACCESS_TOKEN, jwtToken.getAccessToken()));
        response.setHeader(SET_COOKIE, createCookie(REFRESH_TOKEN, jwtToken.getRefreshToken()));

        return true;
    }

    @PostMapping("/login/google")
    public boolean loginGoogleByUserGoogleAuthInfo(
            @RequestBody UserGoogleAuthToken userGoogleAuthToken, HttpServletResponse response) {
        System.out.println("controller");
        JwtToken jwtToken = userAuthService.createJwtTokenByUserGoogleAuthToken(userGoogleAuthToken);

        response.setHeader(SET_COOKIE, createCookie(ACCESS_TOKEN, jwtToken.getAccessToken()));
        response.setHeader(SET_COOKIE, createCookie(REFRESH_TOKEN, jwtToken.getRefreshToken()));

        return true;
    }

    private String createCookie(String tokenType, String token) {
        int expireTime = tokenType.equals(ACCESS_TOKEN) ?
                JwtProperty.ACCESS_TOKEN_EXPIRE_TIME : JwtProperty.REFRESH_TOKEN_EXPIRE_TIME;

        return ResponseCookie.from(tokenType, token)
                .maxAge(expireTime / 1000)
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build()
                .toString();
    }
}
