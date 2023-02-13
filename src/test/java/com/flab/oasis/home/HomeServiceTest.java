package com.flab.oasis.home;

import com.flab.oasis.mapper.HomeMapper;
import com.flab.oasis.model.BookSuggestion;
import com.flab.oasis.service.HomeService;
import com.flab.oasis.constant.SuggestionType;
import org.junit.jupiter.api.Assertions;
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

    @Resource
    CacheManager cacheManager;

    @Test
    void ehCacheTest() {
        List<BookSuggestion> bookList =  homeService.suggestion(
                "test@naver.com", SuggestionType.valueOf("newBook".toUpperCase())
        );

        Cache cache = cacheManager.getCache("homeCache");

        Assertions.assertNotNull(cache);

        Object bookCache = cache.get(
                "suggestion_test@naver.com_" + SuggestionType.valueOf("newBook".toUpperCase()).toString()
        );

        Assertions.assertEquals(bookList, ((SimpleValueWrapper) bookCache).get());
    }
}
