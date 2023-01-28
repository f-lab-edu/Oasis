package com.flab.oasis.home;

import com.flab.oasis.mapper.home.HomeMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.service.home.HomeService;
import com.flab.oasis.service.home.SuggestionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.support.SimpleValueWrapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class HomeServiceTest {
    @Autowired
    HomeService homeService;

    @Autowired
    HomeMapper homeMapper;

    @Resource
    CacheManager cacheManager;

    @Test
    void ehCacheTest() {
        List<Book> bookList =  homeService.suggestion(
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
