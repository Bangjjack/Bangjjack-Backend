package com.project.bangjjack.global.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String AUTH_CODE_PREFIX = "auth:code:";
    private static final Duration AUTH_CODE_TTL = Duration.ofMinutes(5);

    private static final String WS_TOKEN_PREFIX = "ws:token:";
    private static final Duration WS_TOKEN_TTL = Duration.ofMinutes(5);

    private final RedissonClient redissonClient;

    public String createAuthorizationCode(Long userId) {
        String uuid = UUID.randomUUID().toString();
        RBucket<String> bucket = redissonClient.getBucket(AUTH_CODE_PREFIX + uuid);
        bucket.set(String.valueOf(userId), AUTH_CODE_TTL);
        return uuid;
    }

    public String validateAndConsumeAuthorizationCode(String authCode) {
        RBucket<String> bucket = redissonClient.getBucket(AUTH_CODE_PREFIX + authCode);
        return bucket.getAndDelete();
    }

    public String createWsToken(Long userId, String memberName) {
        String uuid = UUID.randomUUID().toString();
        RBucket<String> bucket = redissonClient.getBucket(WS_TOKEN_PREFIX + uuid);
        bucket.set(userId + ":" + memberName, WS_TOKEN_TTL);
        return uuid;
    }

    public String validateAndConsumeWsToken(String wsToken) {
        RBucket<String> bucket = redissonClient.getBucket(WS_TOKEN_PREFIX + wsToken);
        return bucket.getAndDelete();
    }
}
