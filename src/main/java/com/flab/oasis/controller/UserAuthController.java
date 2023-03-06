package com.flab.oasis.controller;

import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final JwtService jwtService;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @PostMapping("/refresh")
    public HttpServletResponse reissueJwtToken(
            HttpServletRequest request, HttpServletResponse response) {
        String jwtHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
            String refreshToken = jwtHeader.substring(7);
            JwtToken jwtToken = jwtService.reissueJwtToken(refreshToken);

            response.addCookie(createCookie(jwtToken.getAccessToken()));
            response.setHeader(AUTHORIZATION_HEADER, String.format("%s %s", "Bearer", jwtToken.getRefreshToken()));

            return response;
        }

        throw new AuthorizationException("Refresh Token does not exist.");
    }

    @PostMapping("/login/default")
    public HttpServletResponse loginAuthFromUserLoginRequest(
            UserLoginRequest userLoginRequest, HttpServletResponse response) {
        JwtToken jwtToken = userAuthService.loginAuthFromUserLoginRequest(userLoginRequest);

        response.addCookie(createCookie(jwtToken.getAccessToken()));
        response.setHeader(AUTHORIZATION_HEADER, String.format("%s %s", "Bearer", jwtToken.getRefreshToken()));

        return response;
    }

    private static Cookie createCookie(String accessToken) {
        Cookie cookie = new Cookie("token", accessToken);
        cookie.setMaxAge(JwtProperty.REFRESH_TOKEN_EXPIRE_TIME / 1000); // 초 단위로 변경
        cookie.setHttpOnly(true);

        return cookie;
    }
}
