package com.project.bangjjack.domain.chat.infrastructure.pubsub;

import com.project.bangjjack.domain.chat.application.dto.response.ReadReceiptBroadcast;
import com.project.bangjjack.domain.chat.infrastructure.broadcaster.ChatBroadcaster;
import com.project.bangjjack.global.infrastructure.JsonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReadReceiptPublisher {

    private static final String CHANNEL_PREFIX = "chat:read-receipt:";

    private final RedissonClient redissonClient;
    private final JsonSerializer jsonSerializer;
    private final ChatBroadcaster chatBroadcaster;

    public void publish(Long roomId, ReadReceiptBroadcast broadcast) {
        try {
            String json = jsonSerializer.serialize(broadcast);
            RTopic topic = redissonClient.getTopic(CHANNEL_PREFIX + roomId, StringCodec.INSTANCE);
            topic.publish(json);
            log.debug("[Redis Pub/Sub] 읽음 알림 발행 완료 - channel={}, readerId={}", CHANNEL_PREFIX + roomId, broadcast.readerId());
        } catch (Exception e) {
            log.warn("[Redis Pub/Sub] 읽음 알림 발행 실패 - Fallback 전환. roomId={}, 원인={}", roomId, e.getMessage());
            chatBroadcaster.broadcastReadReceipt(roomId, broadcast);
        }
    }
}
