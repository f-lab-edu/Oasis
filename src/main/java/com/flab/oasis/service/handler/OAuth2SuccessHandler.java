package com.flab.oasis.service.handler;

import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.repository.UserAuthRepository;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.utils.CookieUtils;
import com.flab.oasis.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserAuthRepository userAuthRepository;

    @Value("${client.url}")
    private String clientUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        boolean joinState;

        try {
            userAuthRepository.getUserAuthByUid(oAuth2User.getName());

            joinState = true;
        } catch (AuthorizationException e) {
            LogUtils.info("This UID need to join in oasis service.", oAuth2User.getName());

            joinState = false;
        }

        JwtToken jwtToken = jwtService.createJwtToken(oAuth2User.getName());

        response.addHeader(
                CookieUtils.SET_COOKIE,
                CookieUtils.createCookie(CookieUtils.ACCESS_TOKEN, jwtToken.getAccessToken())
        );
        response.addHeader(
                CookieUtils.SET_COOKIE,
                CookieUtils.createCookie(CookieUtils.REFRESH_TOKEN, jwtToken.getRefreshToken())
        );

        response.sendRedirect(
                UriComponentsBuilder.fromUriString(String.format("%s%s", clientUrl, "/oauth2/redirect"))
                        .queryParam("result", "success")
                        .queryParam("uid", oAuth2User.getName())
                        .queryParam("joinState", joinState)
                        .build()
                        .encode(StandardCharsets.UTF_8)
                        .toUriString()
        );
    }
}
