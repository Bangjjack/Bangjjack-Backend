package com.project.bangjjack.domain.chat.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.ChatConstants;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRedisPublisher {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    public void publish(Long roomId, ChatMessageResponse message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redissonClient.getTopic(ChatConstants.CHAT_ROOM_TOPIC_PREFIX + roomId, StringCodec.INSTANCE).publish(json);
        } catch (Exception e) {
            log.error("채팅 메시지 발행 실패 roomId={} messageId={}", roomId, message.messageId(), e);
        }
    }
}
