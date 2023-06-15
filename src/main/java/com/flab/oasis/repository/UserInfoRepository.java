package com.flab.oasis.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.mapper.user.UserInfoMapper;
import com.flab.oasis.model.UserInfo;
import com.flab.oasis.model.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserInfoRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserInfoMapper userInfoMapper;
    private final ObjectMapper objectMapper;

    public boolean isExistsNickname(String nickname) {
        return userInfoMapper.isExistsNickname(nickname);
    }

    public void createUserInfo(UserInfo userInfo) {
        userInfoMapper.createUserInfo(userInfo);
    }

    public UserInfo getUserInfoByUid(String uid) throws NotFoundException {
        try {
            Object userInfo = redisTemplate.opsForHash().get("UserInfo", uid);

            return objectMapper.convertValue(
                    userInfo != null ? userInfo : handleNotExistUserInfoInRedis(uid),
                    UserInfo.class
            );
        } catch (Exception e) {
            return getUserInfoByUidFromDB(uid);
        }
    }

    private UserInfo handleNotExistUserInfoInRedis(String uid) throws NotFoundException {
        UserInfo userInfo = getUserInfoByUidFromDB(uid);
        redisTemplate.opsForHash().put("UserInfo", userInfo.getUid(), userInfo);

        return userInfo;
    }

    private UserInfo getUserInfoByUidFromDB(String uid) {
        return Optional.ofNullable(userInfoMapper.getUserInfoByUid(uid))
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.NOT_FOUND, "UserInfo does not created.", uid
                ));
    }
}
