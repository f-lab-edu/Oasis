package com.flab.oasis.service.handler;

import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserAuth;
import com.flab.oasis.repository.UserAuthRepository;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserAuthRepository userAuthRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        UserAuth userAuth = userAuthRepository.getUserAuthByUid(oAuth2User.getName());
        JwtToken jwtToken = jwtService.createJwtToken(userAuth.getUid());

        response.addHeader(
                CookieUtils.SET_COOKIE,
                CookieUtils.createCookie(CookieUtils.ACCESS_TOKEN, jwtToken.getAccessToken())
        );
        response.addHeader(
                CookieUtils.SET_COOKIE,
                CookieUtils.createCookie(CookieUtils.REFRESH_TOKEN, jwtToken.getRefreshToken())
        );
    }
}
