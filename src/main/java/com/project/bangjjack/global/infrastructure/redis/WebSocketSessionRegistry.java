package com.project.bangjjack.global.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketSessionRegistry {

    private static final String SESSION_KEY_PREFIX = "ws:sessions:";

    private final RedissonClient redissonClient;

    public void register(Long userId, String sessionId) {
        sessions(userId).add(sessionId);
    }

    public Set<String> getSessionIds(Long userId) {
        return sessions(userId).readAll();
    }

    public void remove(Long userId, String sessionId) {
        sessions(userId).remove(sessionId);
    }

    private RSet<String> sessions(Long userId) {
        return redissonClient.getSet(SESSION_KEY_PREFIX + userId, StringCodec.INSTANCE);
    }
}
