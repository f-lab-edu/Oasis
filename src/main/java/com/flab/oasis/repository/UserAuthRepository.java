package com.flab.oasis.repository;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.mapper.user.UserAuthMapper;
import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.UserSession;
import com.flab.oasis.model.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAuthRepository {
    private final UserAuthMapper userAuthMapper;

    public void createUserAuth(UserAuth userAuth) {
        userAuthMapper.createUserAuth(userAuth);
    }

    public UserAuth getUserAuthByUid(String uid) {
        return Optional.ofNullable(userAuthMapper.getUserAuthByUid(uid))
                .orElseThrow(() -> new AuthenticationException(
                        ErrorCode.UNAUTHORIZED, "User does not exist.", uid
                ));
    }

    @Cacheable(cacheNames = "UserSession", key = "#uid", cacheManager = "redisCacheManager")
    public UserSession getUserSessionByUid(String uid) {
        return Optional.ofNullable(userAuthMapper.getUserSessionByUid(uid))
                .orElseThrow(() -> new AuthenticationException(
                        ErrorCode.UNAUTHORIZED, "User does not exist.", uid
                ));
    }

    @CacheEvict(cacheNames = "UserSession", key = "#userSession.uid", cacheManager = "redisCacheManager")
    public void updateRefreshToken(UserSession userSession) {
        userAuthMapper.updateRefreshToken(userSession);
    }
}
