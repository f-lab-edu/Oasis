package com.flab.oasis.repository;

import com.flab.oasis.constant.JwtProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UserAuthRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setUserRefreshToken(String uid, String refreshToken) {
        redisTemplate.opsForValue().set(
                makeKey(uid),
                refreshToken,
                Duration.ofSeconds(JwtProperty.REFRESH_TOKEN_EXPIRE_TIME / 1000)
        );
    }

    public boolean existUserRefreshToken(String uid, String refreshToken) {
        String redisToken = String.valueOf(redisTemplate.opsForValue().get(makeKey(uid)));

        return refreshToken.equals(redisToken);
    }

    private String makeKey(String uid) {
        return String.format("%s_%s", "RefreshToken", uid);
    }
}
