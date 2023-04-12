package com.flab.oasis.service;

import com.flab.oasis.mapper.book.TestMapper;
import com.flab.oasis.model.Book;
import com.flab.oasis.model.TestModel;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestMapper testMapper;

    public TestModel dbConnectionTest() {
        return testMapper.dbConnectionTest();
    }

    @Cacheable(cacheNames = "testCache", cacheManager = "ehCacheCacheManager")
    public Book ehCacheTest() {
        return testMapper.cacheTest();
    }

    @Cacheable(cacheNames = "testCache", cacheManager = "redisCacheManager")
    public Book redisCacheTest() {
        return testMapper.cacheTest();
    }
}
