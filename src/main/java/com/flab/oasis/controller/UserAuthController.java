package com.flab.oasis.controller;

import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.GoogleOAuthLoginResult;
import com.flab.oasis.model.GoogleOAuthToken;
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

        CookieUtils.setCookieHeader(response, jwtToken);

        return true;
    }

    @PostMapping("/login/google")
    public GoogleOAuthLoginResult loginGoogleByGoogleOAuthToken(
            @RequestBody GoogleOAuthToken googleOAuthToken, HttpServletResponse response) {
        GoogleOAuthLoginResult googleOAuthLoginResult = userAuthService.createJwtTokenByGoogleOAuthToken(
                googleOAuthToken
        );

        CookieUtils.setCookieHeader(response, googleOAuthLoginResult.getJwtToken());

        return googleOAuthLoginResult;
    }
}
