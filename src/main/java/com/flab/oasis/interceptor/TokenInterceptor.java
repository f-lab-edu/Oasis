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
        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
            String accessToken = jwtHeader.substring(7);

            try {
                jwtService.verifyJwt(accessToken);

                return true;
            } catch (TokenExpiredException e) {
                System.out.println(LogUtils.makeErrorLog(
                        ErrorCode.UNAUTHORIZED, "Access Token is Expired.", accessToken
                ));

                String refreshToken = Optional.ofNullable(WebUtils.getCookie(request, "RefreshToken"))
                        .orElseThrow(() -> new AuthorizationException(
                                ErrorCode.UNAUTHORIZED, "Refresh Token does not exist in cookie."
                        )).getValue();

                JwtToken jwtToken = jwtService.reissueJwtToken(refreshToken);

                response.setHeader("Authorization", String.format("%s %s", "Bearer", jwtToken.getAccessToken()));
                response.setHeader("Set-Cookie", createCookie(jwtToken.getRefreshToken()));

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
        }

        return false;
    }

    private String createCookie(String refreshToken) {
        return ResponseCookie.from("RefreshToken", refreshToken)
                .maxAge(JwtProperty.REFRESH_TOKEN_EXPIRE_TIME / 1000)
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build()
                .toString();
    }
}
