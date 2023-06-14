package com.flab.oasis.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.mapper.user.UserCategoryMapper;
import com.flab.oasis.model.UserCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCategoryRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserCategoryMapper userCategoryMapper;
    private final ObjectMapper objectMapper;

    public void createUserCategory(List<UserCategory> userCategoryList) throws SQLException {
        try {
            userCategoryMapper.createUserCategory(userCategoryList);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<UserCategory> getUserCategoryListByUid(String uid) {
        try {
            List<UserCategory> userCategoryList = objectMapper.convertValue(
                    Optional.ofNullable(
                            redisTemplate.opsForHash().get("UserCategory", uid)
                    ).orElse(
                            userCategoryMapper.getUserCategoryListByUid(uid)
                    ),
                    new TypeReference<List<UserCategory>>() {}
            );

            redisTemplate.opsForHash().put("UserCategory", uid, userCategoryList);

            return userCategoryList;
        } catch (Exception e) {
            return userCategoryMapper.getUserCategoryListByUid(uid);
        }
    }
}
