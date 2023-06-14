package com.flab.oasis.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.mapper.user.UserInfoMapper;
import com.flab.oasis.model.UserInfo;
import com.flab.oasis.model.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
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

    public void createUserInfo(UserInfo userInfo) throws SQLException {
        try{
            userInfoMapper.createUserInfo(userInfo);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public UserInfo getUserInfoByUid(String uid) {
        try {
            UserInfo userInfo = objectMapper.convertValue(
                    Optional.ofNullable(
                            redisTemplate.opsForHash().get("UserInfo", uid)
                    ).orElse(
                            getUserInfoByUidFromDB(uid)
                    ),
                    new TypeReference<UserInfo>() {}
            );

            redisTemplate.opsForHash().put("UserInfo", userInfo.getUid(), userInfo);

            return userInfo;
        } catch (Exception e) {
            return getUserInfoByUidFromDB(uid);
        }
    }

    private UserInfo getUserInfoByUidFromDB(String uid) {
        return Optional.ofNullable(userInfoMapper.getUserInfoByUid(uid))
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.NOT_FOUND, "UserInfo does not created.", uid
                ));
    }
}
