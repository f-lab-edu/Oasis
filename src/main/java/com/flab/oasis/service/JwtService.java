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
import com.flab.oasis.constant.UserRole;
import com.flab.oasis.model.JsonWebToken;
import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.exception.AuthenticationException;
import com.flab.oasis.repository.UserAuthRepository;
import com.flab.oasis.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserAuthRepository userAuthRepository;

    private static final String ACCESS_TOKEN = "AccessToken";
    private static final String REFRESH_TOKEN = "RefreshToken";

    public JsonWebToken createJwt(String uid, UserRole userRole) {
        String accessToken = generateToken(uid, ACCESS_TOKEN);
        String refreshToken = generateToken(uid, REFRESH_TOKEN);

        userAuthRepository.updateRefreshToken(new UserSession(uid, refreshToken, userRole));

        return new JsonWebToken(accessToken, refreshToken);
    }

    public void verifyAccessToken(String accessToken) {
        try {
            DecodedJWT decodedJWT = getDecodedJwtWithVerifySignature(accessToken);
            String uid = decodedJWT.getClaim("uid").asString();
            UserSession userSession = userAuthRepository.getUserSessionByUid(uid);

            if (!uid.equals(userSession.getUid())) {
                throw new AuthenticationException(
                        ErrorCode.UNAUTHORIZED, "Invalid user.", uid
                );
            }
        } catch (TokenExpiredException e) {
            throw new AuthenticationException(ErrorCode.UNAUTHORIZED, "Access Token is Expired.", accessToken);
        } catch (SignatureVerificationException | InvalidClaimException e) {
            throw new AuthenticationException(ErrorCode.UNAUTHORIZED, "Invalid Access Token.", accessToken);
        }
    }

    public UserSession verifyRefreshToken(String refreshToken) {
        try {
            DecodedJWT decodedJWT = getDecodedJwtWithVerifySignature(refreshToken);
            String uid = decodedJWT.getClaim("uid").asString();
            UserSession userSession = userAuthRepository.getUserSessionByUid(uid);

            if (!uid.equals(userSession.getUid())) {
                throw new AuthenticationException(
                        ErrorCode.UNAUTHORIZED, "Invalid user.", uid
                );
            }

            if (!refreshToken.equals(userSession.getRefreshToken())) {
                throw new AuthenticationException(
                        ErrorCode.UNAUTHORIZED, "Refresh Token doesn't match.", refreshToken
                );
            }

            return userSession;
        } catch (TokenExpiredException e) {
            throw new AuthenticationException(ErrorCode.UNAUTHORIZED, "Refresh Token is Expired.", refreshToken);
        } catch (SignatureVerificationException | InvalidClaimException e) {
            throw new AuthenticationException(ErrorCode.UNAUTHORIZED, "Invalid Refresh Token.", refreshToken);
        }
    }

    public JsonWebToken reissueJwt(UserSession userSession) {
        DecodedJWT decodedJWT = JWT.decode(userSession.getRefreshToken());

        // refresh token 만료 3일 전이면 신규 토큰을 발급한다.
        if (willExpire(decodedJWT.getExpiresAt())) {
            LogUtils.info("Access and Refresh Token were Reissued.", userSession.getUid());

            return createJwt(userSession.getUid(), userSession.getUserRole());
        }

        // 유효한 refresh token이면 access token만 새로 발급한다.
        LogUtils.info("Access Token was Reissued.", userSession.getUid());

        return new JsonWebToken(generateToken(userSession.getUid(), ACCESS_TOKEN), userSession.getRefreshToken());
    }

    public String getClaim(String token, String claim) {
        return JWT.decode(token).getClaim(claim).asString();
    }

    private DecodedJWT getDecodedJwtWithVerifySignature(String token)
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
                .withClaim("uid", uid)
                .withIssuedAt(issueDate)
                .withExpiresAt(new Date(issueDate.getTime() + expireTime))
                .sign(algorithm);
    }

    private boolean willExpire(Date expiresAt) {
        return (expiresAt.getTime() - new Date().getTime()) < JwtProperty.REFRESH_TOKEN_REISSUE_TIME;
    }
}
