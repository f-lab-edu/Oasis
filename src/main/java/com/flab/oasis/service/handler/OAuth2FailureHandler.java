package com.flab.oasis.service.handler;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.utils.LogUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Value("${client.url}")
    private String clientUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {

        LogUtils.error(ErrorCode.UNAUTHORIZED, exception.getMessage());

        response.sendRedirect(
                UriComponentsBuilder.fromUriString(String.format("%s%s", clientUrl, "/oauth2/redirect"))
                        .queryParam("result", "failure")
                        .queryParam("message", exception.getMessage())
                        .build()
                        .encode(StandardCharsets.UTF_8)
                        .toUriString()
        );
    }
}
