package com.flab.oasis.interceptor;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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

    private static final String ACCESS_TOKEN = "AccessToken";
    private static final String REFRESH_TOKEN = "RefreshToken";

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = Optional.ofNullable(WebUtils.getCookie(request, ACCESS_TOKEN))
                .orElseThrow(() -> new AuthorizationException(
                        ErrorCode.UNAUTHORIZED, "Access Token does not exist in cookie.")
                ).getValue();
        String refreshToken = Optional.ofNullable(WebUtils.getCookie(request, REFRESH_TOKEN))
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

                response.setHeader("Set-Cookie", createCookie(ACCESS_TOKEN, jwtToken.getAccessToken()));
                response.setHeader("Set-Cookie", createCookie(REFRESH_TOKEN, jwtToken.getRefreshToken()));
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

    private String createCookie(String tokenType, String token) {
        int expireTime = tokenType.equals(ACCESS_TOKEN) ?
                JwtProperty.ACCESS_TOKEN_EXPIRE_TIME : JwtProperty.REFRESH_TOKEN_EXPIRE_TIME;

        return ResponseCookie.from(tokenType, token)
                .maxAge(expireTime / 1000)
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build()
                .toString();
    }
}
