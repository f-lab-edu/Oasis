package com.flab.oasis.interceptor;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
        String jwtHeader = request.getHeader("authorization");
        if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
            try {
                DecodedJWT decodedJWT = jwtService.decodeJWT(jwtHeader.substring(7));

                if (jwtService.checkTokenInfoExistInDB(decodedJWT)) {
                    System.out.printf("Access Token ID doesn't exist in DB. - %s\n", decodedJWT.getId());
                    response.sendError(404, "Access Token ID doesn't exist in DB.");
                }

                return true;
            } catch (TokenExpiredException e) {
                System.out.printf("Access Token is Expired - %s\n", jwtHeader.substring(7));
                response.sendError(205, "Access Token is Expired.");
            } catch (SignatureVerificationException | InvalidClaimException e) {
                System.out.printf("Invalid Access Token - %s\n", jwtHeader.substring(7));
                response.sendError(401, "Invalid Access Token.");
            }
        }

        return false;
    }
}
