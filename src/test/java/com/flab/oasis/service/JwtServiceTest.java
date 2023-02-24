package com.flab.oasis.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.mapper.UserAuthMapper;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.exception.AuthorizationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    JwtService jwtService;

    @Mock
    UserAuthMapper userAuthMapper;

    @DisplayName("토큰 신규 발급")
    @Test
    void testCreateToken() {
        Mockito.doNothing()
                .when(userAuthMapper)
                .updateRefreshToken(ArgumentMatchers.any(UserAuth.class));

        Assertions.assertNotNull(jwtService.createJwtToken("test").getAccessToken());
    }

    @DisplayName("Access Token 재발급")
    @Test
    void testReissueAccessToken() throws InterruptedException {
        JwtToken jwtToken = jwtService.createJwtToken("test");

        Mockito.doReturn(true)
                .when(userAuthMapper)
                .existUserAuthByUidAndRefreshToken(ArgumentMatchers.any(UserAuth.class));

        Thread.sleep(1000);

        Assertions.assertNotEquals(
                jwtToken.getAccessToken(),
                jwtService.reissueJwtToken(jwtToken.getRefreshToken()).getAccessToken()
        );
    }

    @DisplayName("Refresh Token이 DB에 존재하지 않을 경우")
    @Test
    void testReissueTokenThrowError() {
        String refreshToken = jwtService.createJwtToken("test").getRefreshToken();

        Mockito.doReturn(false)
                .when(userAuthMapper)
                .existUserAuthByUidAndRefreshToken(ArgumentMatchers.any(UserAuth.class));

        Assertions.assertThrows(AuthorizationException.class, () -> jwtService.reissueJwtToken(refreshToken));
    }

    @DisplayName("Issuer가 다를 경우")
    @Test
    void testDecodeJwtThrowIssuerError() {
        Algorithm algorithm = Algorithm.HMAC256(JwtProperty.SECRET_KEY);
        String accessToken = JWT.create()
                .withIssuer("OtherIssuer")
                .sign(algorithm);

        Assertions.assertThrows(InvalidClaimException.class, () -> jwtService.decodeJWT(accessToken));
    }

    @DisplayName("알고리즘 Secret Key가 다를 경우")
    @Test
    void testDecodeJwtThrowSignatureError() {
        Algorithm algorithm = Algorithm.HMAC256("OtherKey");
        String accessToken = JWT.create()
                .sign(algorithm);

        Assertions.assertThrows(SignatureVerificationException.class, () -> jwtService.decodeJWT(accessToken));
    }

    @DisplayName("만료된 토큰일 경우")
    @Test
    void testDecodeJwtThrowExpired() {
        Algorithm algorithm = Algorithm.HMAC256(JwtProperty.SECRET_KEY);
        Date issueDate = new Date();

        String accessToken = JWT.create()
                .withIssuer(JwtProperty.ISSUER)
                .withIssuedAt(issueDate)
                .withExpiresAt(new Date(issueDate.getTime() + 1))
                .sign(algorithm);

        Assertions.assertThrows(TokenExpiredException.class, () -> jwtService.decodeJWT(accessToken));
    }
}
