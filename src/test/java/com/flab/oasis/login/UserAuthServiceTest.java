package com.flab.oasis.login;

import com.flab.oasis.model.GoogleOAuthLoginRequest;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.model.exception.AuthenticationException;
import com.flab.oasis.repository.UserAuthRepository;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.service.UserAuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {
    @InjectMocks
    UserAuthService userAuthService;

    @Mock
    UserAuthRepository userAuthRepository;

    @Mock
    JwtService jwtService;

    @DisplayName("기본 로그인 테스트")
    @Test
    void testDefaultLogin() {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        JwtToken jwtToken = new JwtToken("", "");

        // UserAuthService의 hashingPassword를 통해 해싱된 비밀번호
        // parameter - password: null, salt: null
        String hashingPassword = "5873df017b7dd58238f91328244dd841e497909e647418e9fa0a0b814baa26e7";

        BDDMockito.given(userAuthRepository.getUserAuthByUid(userLoginRequest.getUid()))
                .willReturn(UserAuth.builder().password(hashingPassword).build());
        BDDMockito.given(jwtService.createJwtToken(userLoginRequest.getUid()))
                .willReturn(jwtToken);

        Assertions.assertEquals(
                userAuthService.createJwtTokenByUserLoginRequest(userLoginRequest).getAccessToken(),
                jwtToken.getAccessToken()
        );
    }

    @DisplayName("기본 로그인 실패 테스트")
    @Test
    void testDefaultLoginFailure() {
        UserLoginRequest userLoginRequest = new UserLoginRequest();

        BDDMockito.given(userAuthRepository.getUserAuthByUid(userLoginRequest.getUid()))
                .willReturn(UserAuth.builder().password("").build());

        Assertions.assertThrows(
                AuthenticationException.class,
                () -> userAuthService.createJwtTokenByUserLoginRequest(userLoginRequest)
        );
    }

    @DisplayName("구글 ID 토큰 오류 테스트")
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