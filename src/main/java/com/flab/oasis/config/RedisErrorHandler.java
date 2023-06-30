package com.flab.oasis.config;

import com.flab.oasis.constant.ErrorCode;
import com.flab.oasis.utils.LogUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class RedisErrorHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        LogUtils.error(
                exception.getClass(),
                ErrorCode.INTERNAL_SERVER_ERROR,
                String.format("[Redis Cache Get] %s", exception.getMessage()),
                String.format("%s::%s", cache.getName(), key)
        );
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        LogUtils.error(
                exception.getClass(),
                ErrorCode.INTERNAL_SERVER_ERROR,
                String.format("[Redis Cache Put] %s", exception.getMessage()),
                String.format("%s::%s::%s", cache.getName(), key, value)
        );
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        LogUtils.error(
                exception.getClass(),
                ErrorCode.INTERNAL_SERVER_ERROR,
                String.format("[Redis Cache Evict] %s", exception.getMessage()),
                String.format("%s::%s", cache.getName(), key)
        );
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        LogUtils.error(
                exception.getClass(),
                ErrorCode.INTERNAL_SERVER_ERROR,
                String.format("[Redis Cache Clear] %s", exception.getMessage()),
                cache.getName()
        );
    }
}
