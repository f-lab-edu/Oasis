package com.flab.oasis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

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
        return Optional.ofNullable((String) hashOperations.get(key, field)).orElse("notExistKey");

    }

    public Boolean existKey(String key) {
        // null일 경우 pipline / transaction이 진행 중이므로 key 존재 여부를 확인할 수 없어 false를 반환한다.
        return Optional.ofNullable(stringRedisTemplate.hasKey(key)).orElse(Boolean.FALSE);
    }
}
