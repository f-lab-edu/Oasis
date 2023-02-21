package com.flab.oasis.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("POST")) {
            authJwtToken(request, response, handler);
        }

        return true;
    }

    private boolean authJwtToken(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // jwt 인증 프로세스 호출
        return true;
    }
}
