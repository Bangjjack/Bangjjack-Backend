package com.project.bangjjack.domain.chat.infrastructure;

import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;
import com.project.bangjjack.domain.chat.infrastructure.broadcaster.ChatBroadcaster;
import com.project.bangjjack.global.infrastructure.JsonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private static final String CHANNEL_PREFIX = "chat:room:";

    private final RedissonClient redissonClient;
    private final JsonSerializer jsonSerializer;
    private final ChatBroadcaster chatBroadcaster;

    public void publish(Long roomId, ChatMessageBroadcast broadcast) {
        String json = jsonSerializer.serialize(broadcast);
        try {
            RTopic topic = redissonClient.getTopic(CHANNEL_PREFIX + roomId, StringCodec.INSTANCE);
            topic.publish(json);
            log.debug("[Redis Pub/Sub] 메시지 발행 완료 - channel=chat:room:{}, messageId={}", roomId, broadcast.messageId());
        } catch (RedisException e) {
            log.warn("[Redis Pub/Sub] Redis 장애 감지 - Fallback 전환. roomId={}, 원인={}", roomId, e.getMessage());
            chatBroadcaster.broadcastToRoom(roomId, broadcast);
            log.debug("[Redis Pub/Sub] Fallback 브로드캐스트 완료 - roomId={}", roomId);
        }
    }

}
