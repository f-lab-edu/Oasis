package com.flab.oasis.service;

import com.flab.oasis.mapper.UserAuthMapper;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.UserLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserAuthMapper userAuthMapper;
    private final JwtService jwtService;

    public JwtToken loginAuthFromUserLoginRequest(UserLoginRequest userLoginRequest) {
        // password를 hashing한다. -> hashingPassword() 호출
        // db에서 uid에 해당하는 userAuth를 가져온다.
        // hashing된 password와 db의 password가 동일한지 확인한다.
        // password가 동일하면 token을 발급한다. -> jwtService.createJwtToken() 호출
        // password가 동일하지 않으면 AuthorizationException을 던진다.
        return null;
    }

    public void updateRefreshToken(UserAuth userAuth) {
        userAuthMapper.updateRefreshToken(userAuth);
    }

    private String hashingPassword(String password) {
        // key-stretch와 salt를 활용하여 패스워드 해싱
        // key-stretch : 3회
        // salt : timestamp
        return null;
    }
}
