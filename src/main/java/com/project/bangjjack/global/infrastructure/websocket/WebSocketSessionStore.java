package com.project.bangjjack.global.infrastructure.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionStore {

    private final ConcurrentHashMap<Long, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<Long>> sessionRooms = new ConcurrentHashMap<>();

    public void subscribe(Long roomId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionRooms.computeIfAbsent(session.getId(), k -> ConcurrentHashMap.newKeySet()).add(roomId);
    }

    public void unsubscribe(Long roomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
        Set<Long> rooms = sessionRooms.get(session.getId());
        if (rooms != null) {
            rooms.remove(roomId);
        }
    }

    public void removeAll(WebSocketSession session) {
        Set<Long> rooms = sessionRooms.remove(session.getId());
        if (rooms == null) return;
        rooms.forEach(roomId -> {
            Set<WebSocketSession> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        });
    }

    public Set<WebSocketSession> getSessionsByRoom(Long roomId) {
        return roomSessions.getOrDefault(roomId, Collections.emptySet());
    }
}
