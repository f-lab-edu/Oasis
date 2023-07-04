package com.flab.oasis.repository;

import com.flab.oasis.mapper.user.UserCategoryMapper;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserCategoryCount;
import com.flab.oasis.model.UserCategoryCountSelect;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCategoryRepository {
    private final UserCategoryMapper userCategoryMapper;

    public void createUserCategory(List<UserCategory> userCategoryList) {
        userCategoryMapper.createUserCategory(userCategoryList);
    }

    @Cacheable(cacheNames = "UserCategory", key = "#uid", cacheManager = "redisCacheManager")
    public List<UserCategory> getUserCategoryListByUid(String uid) {
        return userCategoryMapper.getUserCategoryListByUid(uid);
    }

    public List<String> getUidListWithOverlappingBookCategory(String uid) {
        return userCategoryMapper.getUidListWithOverlappingBookCategory(uid);
    }

    public List<UserCategoryCount> getUserCategoryCountList(UserCategoryCountSelect userCategoryCountSelect) {
        return userCategoryMapper.getUserCategoryCountList(userCategoryCountSelect);
    }
}
