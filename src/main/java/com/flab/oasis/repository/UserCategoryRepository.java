package com.flab.oasis.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.mapper.user.UserCategoryMapper;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCategoryRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserCategoryMapper userCategoryMapper;
    private final ObjectMapper objectMapper;

    public void createUserCategory(List<UserCategory> userCategoryList) {
        userCategoryMapper.createUserCategory(userCategoryList);
    }

    public List<UserCategory> getUserCategoryByUid(String uid) {
        try {
            List<UserCategory> userCategoryList = objectMapper.convertValue(
                    Optional.ofNullable(
                            redisTemplate.opsForHash().get("UserCategory", uid)
                    ).orElse(
                            getUserCategoryByUidFromDB(uid)
                    ),
                    new TypeReference<List<UserCategory>>() {}
            );

            redisTemplate.opsForHash().put("UserCategory", uid, userCategoryList);

            return userCategoryList;
        } catch (Exception e) {
            return getUserCategoryByUidFromDB(uid);
        }
    }

    private List<UserCategory> getUserCategoryByUidFromDB(String uid) {
        return Optional.ofNullable(userCategoryMapper.getUserCategoryByUid(uid))
                .orElse(new ArrayList<>());
    }
}
