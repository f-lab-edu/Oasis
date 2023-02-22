package com.flab.oasis.service;

import com.flab.oasis.mapper.UserAuthMapper;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.model.exception.AuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserAuthMapper userAuthMapper;
    private final JwtService jwtService;

    public JwtToken loginAuthFromUserLoginRequest(UserLoginRequest userLoginRequest) {
        String salt = String.valueOf(new Date().getTime());
        String hashingPassword = hashingPassword(userLoginRequest.getPassword(), salt);
        UserAuth userAuth = userAuthMapper.getUserAuthByUid(userLoginRequest.getUid());

        if (!userAuth.getPassword().equals(hashingPassword)) {
            throw new AuthorizationException("Password doesn't match.");
        }

        return jwtService.createJwtToken(userAuth.getUid());
    }

    private String hashingPassword(String password, String salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // password에 salt 더하여 key-stretch를 3회 반복한다.
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
