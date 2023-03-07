package com.flab.oasis.login;

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
import org.mockito.Mockito;
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
        String originUid = "uid";
        String accessToken = jwtService.createJwtToken(originUid).getAccessToken();
        String actualUid = jwtService.verifyJwt(accessToken).getSubject();

        Assertions.assertEquals(originUid, actualUid);
    }

    @DisplayName("Refresh Token으로 Access Token 재발급")
    @Test
    void testReissueAccessToken() {
        String originUid = "test";
        String refreshToken = jwtService.createJwtToken(originUid).getRefreshToken();

        Mockito.doReturn(true)
                .when(userAuthRepository)
                .existUserRefreshToken(originUid, refreshToken);

        BDDMockito.given(userAuthRepository.existUserRefreshToken(originUid, refreshToken))
                .willReturn(true);

        String accessToken = jwtService.reissueJwtToken(refreshToken).getAccessToken();
        String actualUid = jwtService.verifyJwt(accessToken).getSubject();

        // refresh token으로 재발급된 access token 검증
        Assertions.assertEquals(originUid, actualUid);
    }

    @DisplayName("Refresh Token이 DB에 존재하지 않을 경우")
    @Test
    void testReissueTokenThrowError() {
        String uid = "test";
        String refreshToken = jwtService.createJwtToken(uid).getRefreshToken();

        Mockito.doReturn(false)
                .when(userAuthRepository)
                .existUserRefreshToken(uid, refreshToken);

        Assertions.assertThrows(AuthorizationException.class, () -> jwtService.reissueJwtToken(refreshToken));
    }

}
