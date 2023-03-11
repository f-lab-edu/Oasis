package com.flab.oasis.controller;

import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.model.JwtToken;
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

    @PostMapping("/refresh")
    public boolean reissueJwtToken(@CookieValue("RefreshToken") String refreshToken, HttpServletResponse response) {
        JwtToken jwtToken = jwtService.reissueJwtToken(refreshToken);

        response.setHeader(AUTHORIZATION_HEADER, makeAuthorizationValue(jwtToken.getAccessToken()));
        response.setHeader(SET_COOKIE, createCookie(jwtToken.getRefreshToken()));

        return true;
    }

    @PostMapping("/login/default")
    public boolean loginAuthFromUserLoginRequest(
            @RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        JwtToken jwtToken = userAuthService.createJwtTokenByUserLoginRequest(userLoginRequest);

        response.setHeader(SET_COOKIE, createCookie("AccessToken", jwtToken.getAccessToken()));
        response.setHeader(SET_COOKIE, createCookie("RefreshToken", jwtToken.getRefreshToken()));

        return true;
    }

    private String createCookie(String tokenType, String token) {
        int expireTime = tokenType.equals("AccessToken") ?
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
