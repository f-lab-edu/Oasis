package com.flab.oasis.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.flab.oasis.constant.JwtExpiredState;
import com.flab.oasis.mapper.UserAuthMapper;
import com.flab.oasis.model.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserAuthMapper userAuthMapper;

    public JwtToken createJwtToken(String uid) {
        // 신규 토큰을 발급한다.
        // 발급된 토큰의 refresh token은 UserAuth 테이블에 반영한다. -> userAuthMapper.updateRefreshToken() 호출

        return null;
    }

    public JwtToken reissueJwtToken(String refreshToken) {
        // Refresh Token을 decode한다.
        // Refresh Token이 유효한지 검사한다. -> validateToken() 호출
        // Refresh Token이 만료되었는지 확인한다. -> isExpired() 호출
        // isExpired의 결과가 WILL_EXPIRED면 토큰을 신규 발급한다. -> createJwtToken() 호출
        // isExpired의 결과가 NOT_EXPIRED면, Access Token을 신규 발급한다. -> generateToken() 호출
        // isExpired의 결과가 EXPIRED면 TokenExpiredException을 던진다.

        return null;
    }

    private String generateToken(String uid, String subject, int expireTime) {
        // token을 발급한다.
        // jti(JWI ID)는 uid다.
        // sub(subject)는 AccessToken 또는 RefreshToken이다.
        return null;
    }

    private DecodedJWT decodeJWT(String token) throws TokenExpiredException {
        return null;
    }

    public boolean validateToken(DecodedJWT decodedJWT) {
        // Token payload의 issuer가 유효한지 확인한다.
        // Token payload의 subject가 Access Token이면 jit에 해당하는 정보가 USER_AUTH 테이블에 존재하는지 확인한다. -> userAuthMapper.existUserAuthByUid() 호출
        // Token payload의 subject가 Refresh Token이면 jit와 refresh token이 USER_AUTH 테이블에 존재하는지 확인한다. -> userAuthMapper.existUserAuthByUidAndRefreshToken() 호출
        // 위 조건들에 해당하지 않으면 AuthorizationException을 던진다.
        return true;
    }

    public JwtExpiredState isExpired(DecodedJWT decodedJWT) {
        // Token payload의 subject에 해당하는 token의 만료여부를 검사한다.
        // subject가 Refresh Token이고, NOT_EXPIRED면 만료 1일 전인지 확인한다.
        // 만료 1일 전이면 WILL_EXPIRED를 반환한다.
        return JwtExpiredState.NOT_EXPIRED;
    }
}
