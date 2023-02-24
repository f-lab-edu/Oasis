package com.flab.oasis.controller;

import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final JwtService jwtService;

    @PostMapping("/refresh")
    public HttpServletResponse reissueJwtToken(
            @CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        JwtToken jwtToken = jwtService.reissueJwtToken(refreshToken);

        response.setHeader("Authorization", String.format("%s %s", "Bearer", jwtToken.getAccessToken()));
        response.addCookie(createCookie(jwtToken.getRefreshToken()));

        return response;
    }

    @PostMapping("/login/default")
    public HttpServletResponse loginAuthFromUserLoginRequest(
            UserLoginRequest userLoginRequest, HttpServletResponse response) {
        JwtToken jwtToken = userAuthService.loginAuthFromUserLoginRequest(userLoginRequest);

        response.setHeader("Authorization", String.format("%s %s", "Bearer", jwtToken.getAccessToken()));
        response.addCookie(createCookie(jwtToken.getRefreshToken()));

        return response;
    }

    private static Cookie createCookie(String refreshToken) {
        Cookie cookie = new Cookie("RefreshToken", refreshToken);
        cookie.setMaxAge(JwtProperty.REFRESH_TOKEN_EXPIRE_TIME / 1000); // 초 단위로 변경

        return cookie;
    }
}
