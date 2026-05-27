package com.project.bangjjack.domain.post.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.post.application.dto.response.AiRecommendedPostResponse;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.port.cache.AiRecommendedPostCachePort;
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
public class RedisAiRecommendedPostCacheAdapter implements AiRecommendedPostCachePort {

    private static final String KEY_PREFIX = "ai-recommended:user:";
    private static final String ROOM_SIZE_ALL = "ALL";
    private static final TypeReference<List<AiRecommendedPostResponse>> LIST_TYPE = new TypeReference<>() {
    };

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<List<AiRecommendedPostResponse>> find(Long userId, RoomSize roomSize) {
        String key = buildKey(userId, roomSize);
        try {
            RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
            String json = bucket.get();
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, LIST_TYPE));
        } catch (Exception e) {
            log.warn("AI recommended cache find failed: key={}, error={}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void save(Long userId, RoomSize roomSize, List<AiRecommendedPostResponse> value, Duration ttl) {
        String key = buildKey(userId, roomSize);
        try {
            String json = objectMapper.writeValueAsString(value);
            RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
            bucket.set(json, ttl);
        } catch (Exception e) {
            log.warn("AI recommended cache save failed: key={}, error={}", key, e.getMessage());
        }
    }

    private String buildKey(Long userId, RoomSize roomSize) {
        return KEY_PREFIX + userId
                + ":roomSize:" + (roomSize == null ? ROOM_SIZE_ALL : roomSize.name());
    }
}
