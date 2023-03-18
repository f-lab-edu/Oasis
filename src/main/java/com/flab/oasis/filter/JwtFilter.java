package com.flab.oasis.filter;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private static final String ACCESS_TOKEN = "AccessToken";
    private static final String REFRESH_TOKEN = "RefreshToken";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String accessToken = Optional.ofNullable(WebUtils.getCookie(request, ACCESS_TOKEN))
                .orElseThrow(() -> new AuthorizationException(
                        ErrorCode.UNAUTHORIZED, "Access Token does not exist in cookie.")
                ).getValue();
        String refreshToken = Optional.ofNullable(WebUtils.getCookie(request, REFRESH_TOKEN))
                .orElseThrow(() -> new AuthorizationException(
                        ErrorCode.UNAUTHORIZED, "Refresh Token does not exist in cookie.")
                ).getValue();

        try {
            jwtService.verifyAccessToken(accessToken);
            jwtService.verifyRefreshToken(refreshToken);

            chain.doFilter(request, response);
        } catch (AuthorizationException e) {
            if (e.getMessage().equals("Access Token is Expired.")) {
                LogUtils.error(
                        ErrorCode.UNAUTHORIZED, "Access Token is Expired.", accessToken
                );

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
