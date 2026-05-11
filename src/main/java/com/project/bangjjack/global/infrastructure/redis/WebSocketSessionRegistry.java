package com.project.bangjjack.global.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebSocketSessionRegistry {

    private static final String SESSION_MAP_KEY = "ws:sessions";

    private final RedissonClient redissonClient;

    public void register(Long userId, String sessionId) {
        sessions().put(String.valueOf(userId), sessionId);
    }

    public Optional<String> getSessionId(Long userId) {
        return Optional.ofNullable(sessions().get(String.valueOf(userId)));
    }

    public void remove(Long userId, String sessionId) {
        // Last-Connection-Wins: 현재 등록된 sessionId일 때만 제거
        sessions().remove(String.valueOf(userId), sessionId);
    }

    private RMap<String, String> sessions() {
        return redissonClient.getMap(SESSION_MAP_KEY, StringCodec.INSTANCE);
    }
}
