package com.flab.oasis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.TestModel;
import com.flab.oasis.service.TestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Objects;

@SpringBootTest
class CommonTest {

    @Autowired
    RedisCacheManager redisCacheManager;

    @Autowired
    EhCacheCacheManager ehCacheCacheManager;

    @Autowired
    TestService testService;

    @Test
    void redisTest() {
        Book book = testService.redisCacheTest();

        Cache cache = redisCacheManager.getCache("testCache");

        Assertions.assertNotNull(cache);

        SimpleValueWrapper bookCache = (SimpleValueWrapper) cache.get(SimpleKey.EMPTY);

        Assertions.assertEquals(
                book, new ObjectMapper().convertValue(Objects.requireNonNull(bookCache).get(), Book.class)
        );

        cache.clear();
    }

    @Test
    void dbTest() {
        TestModel testModel = testService.dbConnectionTest();

        Assertions.assertEquals(10, testModel.getData());
    }

    @Test
    void ehCacheTest() {
        Book book = testService.ehCacheTest();

        Cache cache = ehCacheCacheManager.getCache("testCache");

        Assertions.assertNotNull(cache);

        Book bookCache = cache.get(SimpleKey.EMPTY, Book.class);

        Assertions.assertEquals(book, bookCache);

        cache.clear();
    }
}
