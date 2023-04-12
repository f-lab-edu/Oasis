package com.flab.oasis.controller;

import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.service.UserAuthService;
import com.flab.oasis.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/login/default")
    public boolean loginAuthFromUserLoginRequest(
            @RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        JwtToken jwtToken = userAuthService.createJwtTokenByUserLoginRequest(userLoginRequest);

        response.addHeader(
                CookieUtils.SET_COOKIE,
                CookieUtils.createCookie(CookieUtils.ACCESS_TOKEN, jwtToken.getAccessToken())
        );
        response.addHeader(
                CookieUtils.SET_COOKIE,
                CookieUtils.createCookie(CookieUtils.REFRESH_TOKEN, jwtToken.getRefreshToken())
        );

        return true;
    }

    @PostMapping("/login/google")
    public String loginGoogleByUserGoogleAuthInfo() {
        return "/oauth2/authorization/google";
    }
}
