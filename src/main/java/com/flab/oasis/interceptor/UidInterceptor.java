package com.flab.oasis.interceptor;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UidInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;

    public static final ThreadLocal<String> STRING_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String accessToken = Optional.ofNullable(WebUtils.getCookie(request, CookieUtils.ACCESS_TOKEN))
                .orElseThrow(() -> new AuthorizationException(
                        ErrorCode.UNAUTHORIZED, "Access Token does not exist in cookie.")
                ).getValue();
        STRING_THREAD_LOCAL.set(jwtService.getClaim(accessToken, "uid"));

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        STRING_THREAD_LOCAL.remove();

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
