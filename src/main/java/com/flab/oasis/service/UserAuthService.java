package com.flab.oasis.service;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.mapper.UserAuthMapper;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.model.exception.AuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final JwtService jwtService;
    private final UserAuthMapper userAuthMapper;

    public UserAuth getUserAuthByUid(String uid) {
        return Optional.ofNullable(userAuthMapper.getUserAuthByUid(uid))
                .orElseThrow(() -> new AuthorizationException(
                        ErrorCode.UNAUTHORIZED, "User does not exist.", uid
                ));
    }

    public JwtToken createJwtTokenByUserLoginRequest(UserLoginRequest userLoginRequest) {
        UserAuth userAuth = getUserAuthByUid(userLoginRequest.getUid());
        String hashingPassword = hashingPassword(userLoginRequest.getPassword(), userAuth.getSalt());

        if (!userAuth.getPassword().equals(hashingPassword)) {
            throw new AuthorizationException(
                    ErrorCode.UNAUTHORIZED, "Password does not match.", userLoginRequest.toString()
            );
        }

        return jwtService.createJwtToken(userAuth.getUid());
    }

    public void updateRefreshToken(UserAuth userAuth) {
        userAuthMapper.updateRefreshToken(userAuth);
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
            throw new RuntimeException(e.getMessage());
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
