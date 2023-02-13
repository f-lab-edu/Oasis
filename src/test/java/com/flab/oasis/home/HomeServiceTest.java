package com.flab.oasis.home;

import com.flab.oasis.mapper.HomeMapper;
import com.flab.oasis.mapper.UserMapper;
import com.flab.oasis.model.BookSuggestion;
import com.flab.oasis.model.UserAuth;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserInfo;
import com.flab.oasis.service.HomeService;
import com.flab.oasis.constant.SuggestionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class HomeServiceTest {
    @Autowired
    HomeService homeService;

    @Autowired
    HomeMapper homeMapper;

    @Autowired
    UserMapper userMapper;

    @Resource
    CacheManager cacheManager;

    String uid = "test@naver.com";

    @BeforeEach
    void setup() {
        userMapper.insertUserAuth(
                new UserAuth(uid, "1234", 'N', null, null)
        );
        userMapper.insertUserInfo(
                new UserInfo(uid, "test_user", "hello world!", "www.test.com")
        );
        for (int categoryId : new int[] {101, 102, 103}) {
            userMapper.insertUserCategory(
                    new UserCategory(uid, categoryId)
            );
        }
    }

    @AfterEach
    void clear() {
        userMapper.deleteUserCategoryByUid(uid);
        userMapper.deleteUserInfoByUid(uid);
        userMapper.deleteUserAuthByUid(uid);
    }

    @Test
    void ehCacheTest() {
        List<BookSuggestion> bookList =  homeService.suggestion(
                uid, SuggestionType.valueOf("newBook".toUpperCase())
        );

        Cache cache = cacheManager.getCache("homeCache");

        Assertions.assertNotNull(cache);

        Object bookCache = cache.get(
                String.format(
                        "suggestion_%s_%s",
                        uid,
                        SuggestionType.valueOf("newBook".toUpperCase())
                )
        );

        Assertions.assertEquals(bookList, ((SimpleValueWrapper) bookCache).get());
    }
}
