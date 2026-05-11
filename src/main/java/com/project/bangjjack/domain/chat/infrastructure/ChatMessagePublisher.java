package com.project.bangjjack.domain.chat.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.RedisException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private static final String CHANNEL_PREFIX = "chat:room:";
    private static final String STOMP_DESTINATION_PREFIX = "/sub/chat/rooms/";

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public void publish(Long roomId, ChatMessageBroadcast broadcast) {
        String json = serialize(broadcast);
        try {
            RTopic topic = redissonClient.getTopic(CHANNEL_PREFIX + roomId, StringCodec.INSTANCE);
            topic.publish(json);
        } catch (RedisException e) {
            log.warn("Redis 장애 발생. SimpMessagingTemplate fallback으로 broadcast. roomId={}", roomId);
            messagingTemplate.convertAndSend(STOMP_DESTINATION_PREFIX + roomId, broadcast);
        }
    }

    private String serialize(ChatMessageBroadcast broadcast) {
        try {
            return objectMapper.writeValueAsString(broadcast);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ChatMessageBroadcast 직렬화 실패", e);
        }
    }
}
