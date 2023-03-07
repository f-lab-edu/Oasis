package com.flab.oasis.interceptor;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("POST")) {
            return authJwtToken(request, response, handler);
        }

        return true;
    }

    private boolean authJwtToken(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
            String accessToken = jwtHeader.substring(7);

            try {
                jwtService.verifyJwt(accessToken);

                return true;
            } catch (TokenExpiredException e) {
                System.out.printf("Access Token is Expired - %s%n", accessToken);
                response.sendError(ErrorCode.RESET_CONTENT.getCode(), "Access Token is Expired.");
            } catch (SignatureVerificationException | InvalidClaimException e) {
                System.out.printf("Invalid Access Token - %s%n", accessToken);
                response.sendError(ErrorCode.UNAUTHORIZED.getCode(), "Invalid Access Token.");
            }
        }

        return false;
    }
}
