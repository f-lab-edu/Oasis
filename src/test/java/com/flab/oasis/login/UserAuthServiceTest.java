package com.flab.oasis.login;

import com.flab.oasis.model.GoogleOAuthLoginRequest;
import com.flab.oasis.model.exception.AuthenticationException;
import com.flab.oasis.service.UserAuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {
    @InjectMocks
    UserAuthService userAuthService;

    @Test
    void testInvalidGoogleIdTokenError() {
        GoogleOAuthLoginRequest googleOAuthLoginRequest = new GoogleOAuthLoginRequest();
        googleOAuthLoginRequest.setToken("");

        Assertions.assertThrows(
                AuthenticationException.class,
                () -> userAuthService.createJwtTokenByGoogleOAuthToken(googleOAuthLoginRequest)
        );
    }
}