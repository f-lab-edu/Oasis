package com.flab.oasis.interceptor;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = Optional.ofNullable(WebUtils.getCookie(request, CookieUtils.ACCESS_TOKEN))
                .orElseThrow(() -> new AuthorizationException(
                        ErrorCode.UNAUTHORIZED, "Access Token does not exist in cookie.")
                ).getValue();
        String refreshToken = Optional.ofNullable(WebUtils.getCookie(request, CookieUtils.REFRESH_TOKEN))
                .orElseThrow(() -> new AuthorizationException(
                        ErrorCode.UNAUTHORIZED, "Refresh Token does not exist in cookie.")
                ).getValue();

        try {
            // access token과 refresh token 모두 검증
            jwtService.verifyAccessToken(accessToken);
            jwtService.verifyRefreshToken(refreshToken);

            return true;
        } catch (AuthorizationException e) {
            // access token이 expire되면 refresh token으로 재발급 시도
            if (e.getMessage().equals("Access Token is Expired.")) {
                UserSession userSession = jwtService.verifyRefreshToken(refreshToken);
                JwtToken jwtToken = jwtService.reissueJwtToken(userSession);

                response.addHeader(
                        CookieUtils.SET_COOKIE,
                        CookieUtils.createCookie(CookieUtils.ACCESS_TOKEN, jwtToken.getAccessToken())
                );
                response.addHeader(
                        CookieUtils.SET_COOKIE,
                        CookieUtils.createCookie(CookieUtils.REFRESH_TOKEN, jwtToken.getRefreshToken())
                );
                response.sendError(
                        ErrorCode.RESET_CONTENT.getCode(),
                        "The token was reissued because the access token expired."
                );
            } else {
                response.sendError(e.getErrorCode().getCode(), e.getMessage());
            }

            return false;
        }

    }
}
