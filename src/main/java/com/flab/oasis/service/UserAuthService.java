package com.flab.oasis.service;


import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.model.*;
import com.flab.oasis.model.exception.AuthenticationException;
import com.flab.oasis.model.exception.AuthorizationException;
import com.flab.oasis.model.exception.FatalException;
import com.flab.oasis.repository.UserAuthRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserAuthRepository userAuthRepository;
    private final JwtService jwtService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public boolean createUserAuth(UserAuth userAuth) {
        userAuth.setUid(getAuthorizedUid());
        userAuth.setSalt(String.valueOf(new Date().getTime()));
        userAuth.setPassword(
                hashingPassword(
                        userAuth.getPassword(),
                        userAuth.getSalt()
                )
        );

        userAuthRepository.createUserAuth(userAuth);

        return true;
    }

    public String getAuthorizedUid() {
        return Optional.ofNullable(
                SecurityContextHolder.getContext()
        ).orElseThrow(
                () -> new AuthorizationException(ErrorCode.UNAUTHORIZED, "Unauthorized user.")
        ).getAuthentication().getPrincipal().toString();
    }

    public JwtToken createJwtTokenByUserLoginRequest(UserLoginRequest userLoginRequest) {
        UserAuth userAuth = userAuthRepository.getUserAuthByUid(userLoginRequest.getUid());
        String hashingPassword = hashingPassword(userLoginRequest.getPassword(), userAuth.getSalt());

        if (!userAuth.getPassword().equals(hashingPassword)) {
            throw new AuthenticationException(
                    ErrorCode.UNAUTHORIZED, "Password does not match.", userLoginRequest.toString()
            );
        }

        return jwtService.createJwtToken(userAuth.getUid());
    }

    public GoogleOAuthLoginResult createJwtTokenByGoogleOAuthToken(GoogleOAuthToken googleOAuthToken) {
        String uid = getUidByGoogleOAuthToken(googleOAuthToken.getToken());

        try {
            userAuthRepository.getUserAuthByUid(uid);

            return GoogleOAuthLoginResult.builder()
                    .jwtToken(jwtService.createJwtToken(uid))
                    .joinState(true)
                    .uid(uid)
                    .build();
        } catch (AuthenticationException e) {
            return GoogleOAuthLoginResult.builder()
                    .jwtToken(jwtService.createJwtToken(uid))
                    .joinState(false)
                    .uid(uid)
                    .build();
        }
    }

    private String hashingPassword(String password, String salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // password에 salt를 더하며 key-stretch를 3회 반복한다.
            for (int i = 0; i < 3; i++) {
                String passwordSalt = password + salt;
                messageDigest.update(passwordSalt.getBytes());
                password = byteToString(messageDigest.digest());
            }

            return password;
        } catch (NoSuchAlgorithmException e) {
            throw new FatalException(e, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String getUidByGoogleOAuthToken(String token) {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId)).build();

        try {
            GoogleIdToken.Payload payload = verifier.verify(token).getPayload();

            if (Boolean.TRUE.equals(payload.getEmailVerified())) {
                return payload.getEmail();
            } else {
                throw new AuthenticationException(
                        ErrorCode.UNAUTHORIZED,
                        "This users e-mail address is not verified by Google.",
                        payload.getEmail()
                );
            }
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException(
                    ErrorCode.UNAUTHORIZED, "Invalid Google Auth Token.", token
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new FatalException(e, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String byteToString(byte[] temp) {
        StringBuilder sb = new StringBuilder();
        for(byte a : temp) {
            sb.append(String.format("%02x", a));
        }
        return sb.toString();
    }
}
