package com.flab.oasis.login;

import com.auth0.jwt.JWT;
import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.repository.UserAuthRepository;
import com.flab.oasis.service.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    JwtService jwtService;

    @Mock
    UserAuthRepository userAuthRepository;

    @DisplayName("토큰 신규 발급")
    @Test
    void testCreateToken() {
        String originUid = "test@test.com";
        String accessToken = jwtService.createJwtToken(originUid).getAccessToken();
        String actualUid = JWT.decode(accessToken).getClaim("uid").asString();

        Assertions.assertEquals(originUid, actualUid);
    }

    @DisplayName("Refresh Token으로 Access Token 재발급")
    @Test
    void testReissueAccessToken() {
        String originUid = "test@test.com";
        String refreshToken = jwtService.createJwtToken(originUid).getRefreshToken();
        UserSession userSession = UserSession.builder().uid(originUid).refreshToken(refreshToken).build();

        String accessToken = jwtService.reissueJwtToken(userSession).getAccessToken();
        String actualUid = JWT.decode(accessToken).getClaim("uid").asString();

        // refresh token으로 재발급된 access token 검증
        Assertions.assertEquals(originUid, actualUid);
    }

    @DisplayName("요청된 Refresh Token 값이 저장소의 값과 다를 경우")
    @Test
    void testVerifyRefreshTokenThrowError() {
        String uid = "test@test.com";
        String refreshToken = jwtService.createJwtToken(uid).getRefreshToken();
        UserSession willReturn = UserSession.builder().uid(uid).refreshToken("actualRefreshToken").build();

        BDDMockito.given(userAuthRepository.getUserSessionByUid(uid))
                .willReturn(willReturn);

        Assertions.assertThrows(AuthorizationException.class, () -> jwtService.verifyRefreshToken(refreshToken));
    }

}
