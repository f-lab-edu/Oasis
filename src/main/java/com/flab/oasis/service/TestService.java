package com.flab.oasis.service;

import com.flab.oasis.mapper.book.TestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestMapper testMapper;

    @Cacheable(cacheNames = "testCache", cacheManager = "ehCacheCacheManager")
    public int dataCachingByEhCache() {
        return testMapper.getTestData();
    }

    @CacheEvict(cacheNames = "testCache", cacheManager = "ehCacheCacheManager")
    public void cacheEvictInEhCache() {
    }

    @Cacheable(cacheNames = "testCache", cacheManager = "redisCacheManager")
    public int dataCachingByRedis() {
        return testMapper.getTestData();
    }

    @CacheEvict(cacheNames = "testCache", cacheManager = "redisCacheManager")
    public void cacheEvictInRedis() {
    }
}
