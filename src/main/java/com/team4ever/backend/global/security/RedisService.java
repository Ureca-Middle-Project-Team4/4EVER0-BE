package com.team4ever.backend.global.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class RedisService {

    private static final String REFRESH_KEY_PREFIX = "refresh_token:";
    private final ValueOperations<String,String> ops;
    private final Duration refreshTtl;

    public RedisService(StringRedisTemplate redisTemplate,
                        @Value("${app.auth.refreshTokenExpirationMsec}") long refreshMs) {
        this.ops       = redisTemplate.opsForValue();
        this.refreshTtl = Duration.ofMillis(refreshMs);
    }

    public void storeRefreshToken(String userId, String refreshToken) {
        // 이제 키는 "refresh_token:{userId}", 값은 순수 리프레시 토큰 문자열
        System.out.println("[RedisService] storeRefreshToken 호출 userId=" + userId);
        ops.set(userId, refreshToken, refreshTtl);
    }

    public String getRefreshToken(String userId) {
        return ops.get("refresh_token:" + userId);
    }

    public void deleteRefreshToken(String userId) {
        System.out.println("[RedisService] deleteRefreshToken 호출 userId=" + userId);
        ops.getOperations().delete(userId);
    }
}
