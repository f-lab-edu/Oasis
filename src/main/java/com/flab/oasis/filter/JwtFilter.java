package com.flab.oasis.filter;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.utils.CookieUtils;
import com.flab.oasis.utils.LogUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
        if (!request.getRequestURI().contains("/api/auth")) {
            String accessToken = Optional.ofNullable(WebUtils.getCookie(request, CookieUtils.ACCESS_TOKEN))
                    .orElseThrow(() -> new AuthorizationException(
                            ErrorCode.UNAUTHORIZED, "Access Token does not exist in cookie.")
                    ).getValue();
            String refreshToken = Optional.ofNullable(WebUtils.getCookie(request, CookieUtils.REFRESH_TOKEN))
                    .orElseThrow(() -> new AuthorizationException(
                            ErrorCode.UNAUTHORIZED, "Refresh Token does not exist in cookie.")
                    ).getValue();

            try {
                jwtService.verifyAccessToken(accessToken);
                UserSession userSession = jwtService.verifyRefreshToken(refreshToken);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userSession.getUid(), "", new ArrayList<>()
                );

                // Spring Security Context에 유저의 인증 정보를 등록한다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthorizationException e) {
                if (e.getMessage().equals("Access Token is Expired.")) {
                    LogUtils.error(
                            ErrorCode.UNAUTHORIZED, "Access Token is Expired.", accessToken
                    );

                    UserSession userSession = jwtService.verifyRefreshToken(refreshToken);
                    JwtToken jwtToken = jwtService.reissueJwtToken(userSession);

                    response.addHeader(
                            CookieUtils.SET_COOKIE,
                            CookieUtils.createCookie(CookieUtils.ACCESS_TOKEN, jwtToken.getAccessToken())
                    );
                    response.addHeader(
                            CookieUtils.SET_COOKIE,
                            CookieUtils.createCookie(CookieUtils.REFRESH_TOKEN, jwtToken.getRefreshToken())
                    );

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
