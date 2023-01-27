package com.flab.oasis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;

    public void putHashData(String key, Map<String, String> data) {
        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
        hashOperations.putAll(key, data);
    }

    public String getHashData(String key, String field) {
        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
        return (String) hashOperations.get(key, field);

    }

    public Boolean existKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }
}
