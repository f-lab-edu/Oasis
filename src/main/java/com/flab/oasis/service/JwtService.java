package com.flab.oasis.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.mapper.UserAuthMapper;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.exception.AuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserAuthMapper userAuthMapper;

    private static final String ACCESS_TOKEN = "AccessToken";
    private static final String REFRESH_TOKEN = "RefreshToken";

    public JwtToken createJwtToken(String uid) {
        String accessToken = generateToken(uid, ACCESS_TOKEN);
        String refreshToken = generateToken(uid, REFRESH_TOKEN);

        userAuthMapper.updateRefreshToken(
                UserAuth.builder().uid(uid).refreshToken(refreshToken).build()
        );

        return new JwtToken(accessToken, refreshToken);
    }

    public JwtToken reissueJwtToken(String refreshToken) {
        try {
            DecodedJWT decodedJWT = decodeJWT(refreshToken);

            if (checkTokenInfoExistInDB(decodedJWT)) {
                System.out.printf("Refresh Token doesn't exist in DB - %s", refreshToken);
                throw new AuthorizationException("Refresh Token doesn't exist in DB.");
            }

            // refresh token 만료 1일 전이면 토큰을 새로 발급한다.
            if (willExpire(decodedJWT)) {
                return createJwtToken(decodedJWT.getId());
            }

            // 유효한 refresh token이면 access token만 새로 발급한다.
            return new JwtToken(generateToken(decodedJWT.getId(), ACCESS_TOKEN), refreshToken);
        } catch (TokenExpiredException e) {
            System.out.printf("Refresh Token is Expired - %s\n", refreshToken);
            throw new AuthorizationException("Refresh Token is Expired.");
        } catch (SignatureVerificationException | InvalidClaimException e) {
            System.out.printf("Invalid Refresh Token. - %s\n", refreshToken);
            throw new AuthorizationException("Invalid Refresh Token.");
        }
    }

    public DecodedJWT decodeJWT(String token)
            throws TokenExpiredException, SignatureVerificationException, InvalidClaimException {
        Algorithm algorithm = Algorithm.HMAC256(JwtProperty.SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(JwtProperty.ISSUER)
                .build();

        return verifier.verify(token);
    }

    private String generateToken(String uid, String subject) {
        Algorithm algorithm = Algorithm.HMAC256(JwtProperty.SECRET_KEY);
        Date issueDate = new Date();
        int expireTime = subject.equals(ACCESS_TOKEN) ?
                JwtProperty.ACCESS_TOKEN_EXPIRE_TIME : JwtProperty.REFRESH_TOKEN_EXPIRE_TIME;

        return JWT.create()
                .withIssuer(JwtProperty.ISSUER)
                .withJWTId(uid)
                .withSubject(subject)
                .withIssuedAt(issueDate)
                .withExpiresAt(new Date(issueDate.getTime() + expireTime))
                .sign(algorithm);
    }

    public boolean checkTokenInfoExistInDB(DecodedJWT decodedJWT) {
        if (decodedJWT.getSubject().equals(ACCESS_TOKEN)) {
            return !userAuthMapper.existUserAuthByUid(decodedJWT.getId());
        }

        return !userAuthMapper.existUserAuthByUidAndRefreshToken(
                UserAuth.builder().uid(decodedJWT.getId()).refreshToken(decodedJWT.getToken()).build()
        );
    }

    private boolean willExpire(DecodedJWT decodedJWT) {
        return (decodedJWT.getExpiresAt().getTime() - new Date().getTime()) < JwtProperty.REFRESH_TOKEN_REISSUE_TIME;
    }
}
