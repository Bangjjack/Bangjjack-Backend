package com.project.bangjjack.domain.chat.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageSubscriber {

    private static final String CHANNEL_PATTERN = "chat:room:*";
    private static final String STOMP_DESTINATION_PREFIX = "/sub/chat/rooms/";

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void subscribe() {
        try {
            RPatternTopic patternTopic = redissonClient.getPatternTopic(CHANNEL_PATTERN, StringCodec.INSTANCE);
            patternTopic.addListener(String.class, (pattern, channel, json) -> {
                try {
                    ChatMessageBroadcast broadcast = objectMapper.readValue(json, ChatMessageBroadcast.class);
                    messagingTemplate.convertAndSend(STOMP_DESTINATION_PREFIX + broadcast.roomId(), broadcast);
                } catch (Exception e) {
                    log.error("Redis 메시지 역직렬화 실패. channel={}, json={}", channel, json, e);
                }
            });
            log.info("Redis Pub/Sub 채팅 구독 등록 완료. pattern={}", CHANNEL_PATTERN);
        } catch (Exception e) {
            log.warn("Redis Pub/Sub 구독 등록 실패. Redis 연결 상태를 확인하세요.", e);
        }
    }
}
