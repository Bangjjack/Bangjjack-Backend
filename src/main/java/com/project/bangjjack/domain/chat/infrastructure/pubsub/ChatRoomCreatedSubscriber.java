package com.project.bangjjack.domain.chat.infrastructure.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.application.event.ChatRoomCreatedEvent;
import com.project.bangjjack.global.infrastructure.redis.WebSocketSessionRegistry;
import com.project.bangjjack.global.infrastructure.websocket.WebSocketSessionStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class ChatRoomCreatedSubscriber {

    private static final String CHANNEL = "chat:system:room-created";

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;
    private final WebSocketSessionRegistry sessionRegistry;
    private final WebSocketSessionStore sessionStore;

    @PostConstruct
    public void subscribe() {
        RTopic topic = redissonClient.getTopic(CHANNEL, StringCodec.INSTANCE);
        topic.addListener(String.class, (channel, json) -> {
            try {
                ChatRoomCreatedEvent event = objectMapper.readValue(json, ChatRoomCreatedEvent.class);
                subscribeActiveSessions(event);
            } catch (Exception e) {
                log.error("[Redis Sub] 채팅방 생성 이벤트 처리 실패 - 원인={}", e.getMessage(), e);
            }
        });
        log.info("[Redis Sub] 채팅방 생성 구독 등록 완료 - channel={}", CHANNEL);
    }

    private void subscribeActiveSessions(ChatRoomCreatedEvent event) {
        Long roomId = event.roomId();
        for (Long userId : event.participantIds()) {
            Set<String> sessionIds = sessionRegistry.getSessionIds(userId);
            for (String sessionId : sessionIds) {
                sessionStore.getById(sessionId).ifPresentOrElse(
                        session -> {
                            sessionStore.subscribe(roomId, session);
                            log.debug("[WS] 신규 채팅방 동적 구독 - userId={}, roomId={}, sessionId={}", userId, roomId, sessionId);
                        },
                        () -> {
                            sessionRegistry.remove(userId, sessionId);
                            log.debug("[WS] 스테일 세션 ID 제거 - userId={}, sessionId={}", userId, sessionId);
                        }
                );
            }
        }
    }
}
