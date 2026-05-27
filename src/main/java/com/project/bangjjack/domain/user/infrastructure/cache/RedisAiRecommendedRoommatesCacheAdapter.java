package com.project.bangjjack.domain.user.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.user.application.dto.response.AiRecommendedRoommateResponse;
import com.project.bangjjack.domain.user.domain.port.cache.AiRecommendedRoommatesCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAiRecommendedRoommatesCacheAdapter implements AiRecommendedRoommatesCachePort {

    private static final String KEY_PREFIX = "ai-recommended-roommates:user:";
    private static final TypeReference<List<AiRecommendedRoommateResponse>> LIST_TYPE = new TypeReference<>() {
    };

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<List<AiRecommendedRoommateResponse>> find(Long userId) {
        String key = buildKey(userId);
        try {
            RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
            String json = bucket.get();
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, LIST_TYPE));
        } catch (Exception e) {
            log.warn("AI recommended roommates cache find failed: key={}, error={}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void save(Long userId, List<AiRecommendedRoommateResponse> value, Duration ttl) {
        String key = buildKey(userId);
        try {
            String json = objectMapper.writeValueAsString(value);
            RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
            bucket.set(json, ttl);
        } catch (Exception e) {
            log.warn("AI recommended roommates cache save failed: key={}, error={}", key, e.getMessage());
        }
    }

    private String buildKey(Long userId) {
        return KEY_PREFIX + userId;
    }
}
