package com.flab.oasis.interceptor;

import com.flab.oasis.model.LoginResult;
import com.flab.oasis.utils.CookieUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView
    ) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        LoginResult loginResult = (LoginResult) authentication.getCredentials();
        CookieUtils.setCookieHeader(response, loginResult.getJwtToken());
        response.getOutputStream().print(loginResult.isJoinUser());

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
