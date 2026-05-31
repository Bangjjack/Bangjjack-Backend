package com.project.bangjjack.global.infrastructure.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.global.config.security.oauth2.OAuthAttributes;
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
    private static final Duration WS_TOKEN_TTL = Duration.ofHours(12);

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    public String createAuthorizationCode(OAuthAttributes attributes) {
        String uuid = UUID.randomUUID().toString();
        RBucket<String> bucket = redissonClient.getBucket(AUTH_CODE_PREFIX + uuid);
        bucket.set(serialize(attributes), AUTH_CODE_TTL);
        return uuid;
    }

    public OAuthAttributes validateAndConsumeAuthorizationCode(String authCode) {
        RBucket<String> bucket = redissonClient.getBucket(AUTH_CODE_PREFIX + authCode);
        String value = bucket.getAndDelete();
        if (value == null) {
            return null;
        }
        return deserialize(value);
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

    private String serialize(OAuthAttributes attributes) {
        try {
            return objectMapper.writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("OAuthAttributes 직렬화 실패", e);
        }
    }

    private OAuthAttributes deserialize(String json) {
        try {
            return objectMapper.readValue(json, OAuthAttributes.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("OAuthAttributes 역직렬화 실패", e);
        }
    }
}
