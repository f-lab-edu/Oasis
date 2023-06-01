package com.flab.oasis.filter;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.model.JsonWebToken;
import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.exception.AuthenticationException;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.utils.CookieUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class JwtFilter extends BasicAuthenticationFilter {
    private final JwtService jwtService;

    public JwtFilter(AuthenticationManager authenticationManager, JwtService jwtService) {
        super(authenticationManager);
        this.jwtService = jwtService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 인증 요청은 JwtFilter 과정을 생략한다.
        if (!request.getRequestURI().contains("auth")) {
            String accessToken = Optional.ofNullable(WebUtils.getCookie(request, CookieUtils.ACCESS_TOKEN))
                    .orElseThrow(() -> new AuthenticationException(
                            ErrorCode.UNAUTHORIZED, "Access Token does not exist in cookie.")
                    ).getValue();
            String refreshToken = Optional.ofNullable(WebUtils.getCookie(request, CookieUtils.REFRESH_TOKEN))
                    .orElseThrow(() -> new AuthenticationException(
                            ErrorCode.UNAUTHORIZED, "Refresh Token does not exist in cookie.")
                    ).getValue();

            try {
                jwtService.verifyAccessToken(accessToken);
                UserSession userSession = jwtService.verifyRefreshToken(refreshToken);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userSession.getUid(), "",
                        Collections.singletonList(new SimpleGrantedAuthority(userSession.getUserRole().getRole()))
                );

                // Spring Security Context에 유저의 인증 정보를 등록한다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthorizationException e) {
                if (e.getMessage().equals("Access Token is Expired.")) {
                    UserSession userSession = jwtService.verifyRefreshToken(refreshToken);
                    JsonWebToken jsonWebToken = jwtService.reissueJwt(userSession);

                    CookieUtils.setCookieHeader(response, jsonWebToken);

                    response.sendError(
                            ErrorCode.RESET_CONTENT.getCode(),
                            "The token was reissued because the access token expired."
                    );
                } else {
                    response.sendError(e.getErrorCode().getCode(), e.getMessage());
                }
            }
        }

        chain.doFilter(request, response);
    }
}
