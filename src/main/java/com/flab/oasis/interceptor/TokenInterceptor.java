package com.flab.oasis.interceptor;

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
        // Access Token이 유효한지 검사한다. -> jwtService.validateToken() 호출
        // jwtService.validateToken()의 결과가 true면 Access Token이 만료 되었는지 검사한다. -> jwtService.isExpired() 호출
        // jwtService.isExpired()의 결과가 NOT_EXPIRED면 true를 반환한다.
        // jwtService.isExpired()의 결과가 EXPIRED면 false를 반환한다.

        // AuthorizationException을 catch하면 err message와 err code를 response한다.

        return false;
    }
}
