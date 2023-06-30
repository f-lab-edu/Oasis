package com.flab.oasis.controller;

import com.flab.oasis.model.*;
import com.flab.oasis.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/join")
    public JsonWebToken createUserAuth(@RequestBody UserAuth userAuth) {
        return userAuthService.createUserAuth(userAuth);
    }

    @PostMapping("/login/default")
    public LoginResult loginAuthFromUserLoginRequest(@RequestBody UserLoginRequest userLoginRequest) {
        return userAuthService.tryLoginDefault(userLoginRequest);
    }

    @PostMapping("/login/google")
    public LoginResult loginGoogleByGoogleOAuthToken(@RequestBody GoogleOAuthLoginRequest googleOAuthLoginRequest) {
        return userAuthService.tryLoginGoogle(googleOAuthLoginRequest);
    }
}
