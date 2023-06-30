package com.flab.oasis.login;

import com.auth0.jwt.JWT;
import com.flab.oasis.constant.UserRole;
import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.exception.AuthenticationException;
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
        String accessToken = jwtService.createJwt(originUid, UserRole.USER).getAccessToken();
        String actualUid = JWT.decode(accessToken).getClaim("uid").asString();

        Assertions.assertEquals(originUid, actualUid);
    }

    @DisplayName("Refresh Token으로 Access Token 재발급")
    @Test
    void testReissueAccessToken() {
        String originUid = "test@test.com";
        UserRole userRole = UserRole.USER;
        String refreshToken = jwtService.createJwt(originUid, userRole).getRefreshToken();
        UserSession userSession = new UserSession(originUid, refreshToken, userRole);

        String accessToken = jwtService.reissueJwt(userSession).getAccessToken();
        String actualUid = JWT.decode(accessToken).getClaim("uid").asString();

        // refresh token으로 재발급된 access token 검증
        Assertions.assertEquals(originUid, actualUid);
    }

    @DisplayName("요청된 Refresh Token 값이 저장소의 값과 다를 경우")
    @Test
    void testVerifyRefreshTokenThrowError() {
        String uid = "test@test.com";
        UserRole userRole = UserRole.USER;
        String refreshToken = jwtService.createJwt(uid, userRole).getRefreshToken();
        UserSession willReturn = new UserSession(uid, null, userRole);

        BDDMockito.given(userAuthRepository.getUserSessionByUid(uid))
                .willReturn(willReturn);

        Assertions.assertThrows(AuthenticationException.class, () -> jwtService.verifyRefreshToken(refreshToken));
    }

}
