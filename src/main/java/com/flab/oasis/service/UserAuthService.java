package com.flab.oasis.service;


import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.constant.UserRole;
import com.flab.oasis.model.*;
import com.flab.oasis.model.exception.AuthenticationException;
import com.flab.oasis.model.exception.FatalException;
import com.flab.oasis.repository.UserAuthRepository;
import com.flab.oasis.utils.LogUtils;
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

    public JsonWebToken createUserAuth(UserAuth userAuth) {
        userAuth.setUserRole(UserRole.USER);
        if (userAuth.getSocialYN() == 'N') {
            userAuth.setSalt(String.valueOf(new Date().getTime()));
            userAuth.setPassword(
                    hashingPassword(
                            userAuth.getPassword(),
                            userAuth.getSalt()
                    )
            );
        }

        userAuthRepository.createUserAuth(userAuth);

        LogUtils.info("A new user has been created.", userAuth.getUid());

        return jwtService.createJwt(userAuth.getUid(), userAuth.getUserRole());
    }

    public LoginResult tryLoginDefault(UserLoginRequest userLoginRequest) {
        UserAuth userAuth = userAuthRepository.getUserAuthByUid(userLoginRequest.getUid());
        String hashingPassword = hashingPassword(userLoginRequest.getPassword(), userAuth.getSalt());

        if (!userAuth.getPassword().equals(hashingPassword)) {
            throw new AuthenticationException(
                    ErrorCode.UNAUTHORIZED, "Password does not match.", userLoginRequest.toString()
            );
        }

        return LoginResult.builder()
                .jsonWebToken(
                        jwtService.createJwt(userAuth.getUid(), userAuth.getUserRole())
                )
                .joinUser(true)
                .build();
    }

    public LoginResult tryLoginGoogle(GoogleOAuthLoginRequest googleOAuthLoginRequest) {
        String uid = getUidByGoogleOAuthToken(googleOAuthLoginRequest.getToken());

        try {
            UserAuth userAuth = userAuthRepository.getUserAuthByUid(uid);

            return LoginResult.builder()
                    .jsonWebToken(
                            jwtService.createJwt(uid, userAuth.getUserRole())
                    )
                    .joinUser(true)
                    .build();
        } catch (AuthenticationException e) {
            return LoginResult.builder()
                    .jsonWebToken(null)
                    .joinUser(false)
                    .build();
        }
    }

    public String getAuthenticatedUid() {
        return Optional.ofNullable(
                SecurityContextHolder.getContext()
        ).orElseThrow(
                () -> new AuthenticationException(ErrorCode.UNAUTHORIZED, "Unauthorized user.")
        ).getAuthentication().getPrincipal().toString();
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
            throw new FatalException(
                    ErrorCode.INTERNAL_SERVER_ERROR, FatalException.makeStackTraceMessage(e)
            );
        }
    }

    private String getUidByGoogleOAuthToken(String token) {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId)).build();

        try {
            GoogleIdToken.Payload payload = verifier.verify(token).getPayload();

            if (Boolean.FALSE.equals(payload.getEmailVerified())) {
                throw new AuthenticationException(
                        ErrorCode.UNAUTHORIZED,
                        "This users e-mail address is not verified by Google.",
                        payload.getEmail()
                );
            }

            return payload.getEmail();
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException(
                    ErrorCode.UNAUTHORIZED, "Invalid Google OAuth Token.", token
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new FatalException(
                    ErrorCode.INTERNAL_SERVER_ERROR, FatalException.makeStackTraceMessage(e)
            );
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
