package com.flab.oasis.controller;

import com.flab.oasis.model.*;
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

    @PostMapping("/join")
    public boolean createUserAuth(@RequestBody UserAuth userAuth) {
        return userAuthService.createUserAuth(userAuth);
    }

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
    public GoogleOAuthLoginResult loginGoogleByGoogleOAuthToken(
            @RequestBody GoogleOAuthToken googleOAuthToken, HttpServletResponse response) {
        GoogleOAuthLoginResult googleOAuthLoginResult = userAuthService.createJwtTokenByGoogleOAuthToken(
                googleOAuthToken
        );

        response.addHeader(
                CookieUtils.SET_COOKIE,
                CookieUtils.createCookie(CookieUtils.ACCESS_TOKEN, googleOAuthLoginResult.getJwtToken().getAccessToken())
        );
        response.addHeader(
                CookieUtils.SET_COOKIE,
                CookieUtils.createCookie(CookieUtils.REFRESH_TOKEN, googleOAuthLoginResult.getJwtToken().getRefreshToken())
        );

        return googleOAuthLoginResult;
    }
}
