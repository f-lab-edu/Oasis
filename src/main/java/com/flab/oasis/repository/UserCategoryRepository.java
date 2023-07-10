package com.flab.oasis.repository;

import com.flab.oasis.constant.BookCategory;
import com.flab.oasis.mapper.user.UserCategoryMapper;
import com.flab.oasis.model.UserCategory;
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

    @Cacheable(cacheNames = "uidListByBookCategory", key = "#bookCategory", cacheManager = "redisCacheManager")
    public List<String> getUidListByBookCategory(BookCategory bookCategory) {
        return userCategoryMapper.getUidListByBookCategory(bookCategory);
    }
}
