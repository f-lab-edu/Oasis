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

    private static final String AUTHORIZATION_HEADER = "Authorization";
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

        response.setHeader(AUTHORIZATION_HEADER, makeAuthorizationValue(jwtToken.getAccessToken()));
        response.setHeader(SET_COOKIE, createCookie(jwtToken.getRefreshToken()));

        return true;
    }

    private String makeAuthorizationValue(String accessToken) {
        return String.format("%s %s", "Bearer", accessToken);
    }

    private String createCookie(String refreshToken) {
        return ResponseCookie.from("RefreshToken", refreshToken)
                .maxAge(JwtProperty.REFRESH_TOKEN_EXPIRE_TIME / 1000)
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build()
                .toString();
    }
}
