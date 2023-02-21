package com.flab.oasis.controller;

import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final JwtService jwtService;

    @PostMapping("/refresh")
    public JwtToken reissueJwtToken(@CookieValue("refreshToken") String refreshToken) {
        return jwtService.reissueJwtToken(refreshToken);
    }

    @PostMapping("/login/default")
    public JwtToken loginAuthFromUserLoginRequest(UserLoginRequest userLoginRequest) {
        return userAuthService.loginAuthFromUserLoginRequest(userLoginRequest);
    }
}
