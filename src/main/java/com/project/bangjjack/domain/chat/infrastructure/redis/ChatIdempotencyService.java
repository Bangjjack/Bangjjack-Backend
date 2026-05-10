package com.project.bangjjack.domain.chat.infrastructure.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.ChatConstants;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageAckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatIdempotencyService {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    public Optional<ChatMessageAckResponse> findDuplicate(Long userId, String clientTempId) {
        String json = redissonClient.<String>getBucket(buildKey(userId, clientTempId), StringCodec.INSTANCE).get();
        if (json == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, ChatMessageAckResponse.class));
        } catch (JsonProcessingException e) {
            log.warn("멱등성 캐시 역직렬화 실패 userId={} clientTempId={}", userId, clientTempId, e);
            return Optional.empty();
        }
    }

    public void store(Long userId, String clientTempId, ChatMessageAckResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redissonClient.<String>getBucket(buildKey(userId, clientTempId), StringCodec.INSTANCE)
                    .set(json, ChatConstants.IDEMPOTENCY_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("멱등성 캐시 저장 실패 userId={} clientTempId={}", userId, clientTempId, e);
        }
    }

    private String buildKey(Long userId, String clientTempId) {
        return ChatConstants.IDEMPOTENCY_KEY_PREFIX + userId + "." + clientTempId;
    }
}
