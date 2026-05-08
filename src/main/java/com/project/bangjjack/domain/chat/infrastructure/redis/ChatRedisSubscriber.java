package com.project.bangjjack.domain.chat.infrastructure.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRedisSubscriber {

    private final RedissonClient redissonClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void subscribe() {
        redissonClient.getPatternTopic("chat.room.*", StringCodec.INSTANCE)
                .addListener(String.class, (pattern, channel, json) -> {
                    try {
                        String roomId = channel.toString().replace("chat.room.", "");
                        ChatMessageResponse message = objectMapper.readValue(json, ChatMessageResponse.class);
                        messagingTemplate.convertAndSend("/sub/chat-rooms/" + roomId, message);
                    } catch (JsonProcessingException e) {
                        log.error("채팅 메시지 역직렬화 실패 channel={}", channel, e);
                    }
                });
    }
}
