package com.flab.oasis.interceptor;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        if (request.getMethod().equals("POST")) {
            return authJwtToken(request, response);
        }

        return true;
    }

    private boolean authJwtToken(
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = Optional.ofNullable(WebUtils.getCookie(request, ACCESS_TOKEN))
                .orElseThrow(() -> new AuthorizationException(
                        ErrorCode.UNAUTHORIZED, "Access Token does not exist in cookie.")
                ).getValue();

        try {
            jwtService.verifyJwt(accessToken);

            return true;
        } catch (TokenExpiredException e) {
            System.out.println(LogUtils.makeErrorLog(
                    ErrorCode.UNAUTHORIZED, "Access Token is Expired.", accessToken
            ));

            String refreshToken = Optional.ofNullable(WebUtils.getCookie(request, REFRESH_TOKEN))
                    .orElseThrow(() -> new AuthorizationException(
                            ErrorCode.UNAUTHORIZED, "Refresh Token does not exist in cookie."
                    )).getValue();

            JwtToken jwtToken = jwtService.reissueJwtToken(refreshToken);

            response.setHeader("Set-Cookie", createCookie(ACCESS_TOKEN, jwtToken.getAccessToken()));
            response.setHeader("Set-Cookie", createCookie(REFRESH_TOKEN, jwtToken.getRefreshToken()));

            response.sendError(
                    ErrorCode.RESET_CONTENT.getCode(),
                    "The token was reissued because the access token expired."
            );
        } catch (SignatureVerificationException | InvalidClaimException e) {
            System.out.println(LogUtils.makeErrorLog(
                    ErrorCode.UNAUTHORIZED, "Invalid Access Token.", accessToken
            ));

            response.sendError(ErrorCode.UNAUTHORIZED.getCode(), "Invalid Access Token.");
        }

        return false;
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
