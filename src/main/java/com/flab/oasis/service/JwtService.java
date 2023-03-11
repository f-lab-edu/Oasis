package com.flab.oasis.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.constant.JwtProperty;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ACCESS_TOKEN = "AccessToken";
    private static final String REFRESH_TOKEN = "RefreshToken";

    public JwtToken createJwtToken(String uid) {
        String accessToken = generateToken(uid, ACCESS_TOKEN);
        String refreshToken = generateToken(uid, REFRESH_TOKEN);

        redisTemplate.opsForValue().set(
                makeKey(uid),
                refreshToken,
                Duration.ofSeconds(JwtProperty.REFRESH_TOKEN_EXPIRE_TIME / 1000)
        );

        return new JwtToken(accessToken, refreshToken);
    }

    public JwtToken reissueJwtToken(String refreshToken) {
        try {
            DecodedJWT decodedJWT = verifyJwt(refreshToken);
            String uid = decodedJWT.getSubject();

            // refresh token이 redis에 존재하지 않으면 다시 로그인을 하도록 한다.
            if (!refreshToken.equals(String.valueOf(redisTemplate.opsForValue().get(makeKey(uid))))) {
                throw new AuthorizationException(
                        ErrorCode.NOT_FOUND, "Refresh Token doesn't exist in Redis.", refreshToken
                );
            }

            // refresh token 만료 3일 전이면 토큰을 새로 발급한다.
            if (willExpire(decodedJWT.getExpiresAt())) {
                System.out.println(LogUtils.makeLog("Access/Refresh Token wes Reissued.", uid));

                return createJwtToken(uid);
            }

            // 유효한 refresh token이면 access token만 새로 발급한다.
            System.out.println(LogUtils.makeLog("Access Token wes Reissued.", uid));

            return new JwtToken(generateToken(uid, ACCESS_TOKEN), refreshToken);
        } catch (TokenExpiredException e) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED, "Refresh Token is Expired.", refreshToken);
        } catch (SignatureVerificationException | InvalidClaimException e) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED, "Invalid Refresh Token.", refreshToken);
        }
    }

    public DecodedJWT verifyJwt(String token)
            throws TokenExpiredException, SignatureVerificationException, InvalidClaimException {
        Algorithm algorithm = Algorithm.HMAC256(JwtProperty.SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(JwtProperty.ISSUER)
                .build();

        return verifier.verify(token);
    }

    private String generateToken(String uid, String tokenType) {
        Algorithm algorithm = Algorithm.HMAC256(JwtProperty.SECRET_KEY);
        Date issueDate = new Date();
        int expireTime = tokenType.equals(ACCESS_TOKEN) ?
                JwtProperty.ACCESS_TOKEN_EXPIRE_TIME : JwtProperty.REFRESH_TOKEN_EXPIRE_TIME;

        return JWT.create()
                .withIssuer(JwtProperty.ISSUER)
                .withSubject(uid)
                .withIssuedAt(issueDate)
                .withExpiresAt(new Date(issueDate.getTime() + expireTime))
                .sign(algorithm);
    }

    private String makeKey(String uid) {
        return String.format("RefreshToken_%s", uid);
    }

    private boolean willExpire(Date expiresAt) {
        return (expiresAt.getTime() - new Date().getTime()) < JwtProperty.REFRESH_TOKEN_REISSUE_TIME;
    }
}
