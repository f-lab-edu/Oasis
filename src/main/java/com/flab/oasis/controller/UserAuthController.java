package com.flab.oasis.controller;

import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.GoogleOAuthLoginResult;
import com.flab.oasis.model.GoogleOAuthLoginRequest;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/login/default")
    public boolean loginAuthFromUserLoginRequest(
            @RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        setSecurityContext(
                userLoginRequest.getUid(),
                userAuthService.createJwtTokenByUserLoginRequest(userLoginRequest)
        );

        return true;
    }

    @PostMapping("/login/google")
    public boolean loginGoogleByGoogleOAuthToken(
            @RequestBody GoogleOAuthLoginRequest googleOAuthLoginRequest, HttpServletResponse response) {
        GoogleOAuthLoginResult googleOAuthLoginResult = userAuthService.createJwtTokenByGoogleOAuthToken(
                googleOAuthLoginRequest
        );

        if (googleOAuthLoginResult.isJoinState()) {
            setSecurityContext(googleOAuthLoginResult.getUid(), googleOAuthLoginResult.getJwtToken());
        }

        return googleOAuthLoginResult.isJoinState();
    }

    private void setSecurityContext(String uid, JwtToken jwtToken) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                uid, jwtToken, new ArrayList<>()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
