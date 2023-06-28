package com.flab.oasis.repository;

import com.flab.oasis.mapper.user.UserRelationMapper;
import com.flab.oasis.model.UserRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRelationRepository {
    private final UserRelationMapper userRelationMapper;

    @Cacheable(cacheNames = "UserRelation", key = "#uid", cacheManager = "redisCacheManager")
    public List<UserRelation> getUserRelationListByUid(String uid) {
        return userRelationMapper.getUserRelationListByUid(uid);
    }

    @CacheEvict(cacheNames = "UserRelation", key = "#userRelation.uid", cacheManager = "redisCacheManager")
    public void createUserRelation(UserRelation userRelation) {
        userRelationMapper.createUserRelation(userRelation);
    }
}
