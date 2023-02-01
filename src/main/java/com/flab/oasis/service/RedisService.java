package com.flab.oasis.service;

import com.flab.oasis.constant.RedisKey;
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

    public Object getHashData(RedisKey key, String field) {
        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
        String jsonString = Optional.ofNullable((String) hashOperations.get(key.name(), field))
                .orElseThrow(() -> new RuntimeException("notExistKey"));

        return key.jsonStringToModel(jsonString);
    }

    public Boolean existKey(String key) {
        // null일 경우 pipline / transaction이 진행 중이므로 key 존재 여부를 확인할 수 없어 false를 반환한다.
        return Optional.ofNullable(stringRedisTemplate.hasKey(key)).orElse(Boolean.FALSE);
    }
}
