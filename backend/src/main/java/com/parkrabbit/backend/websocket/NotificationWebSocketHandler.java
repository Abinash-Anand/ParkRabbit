package com.parkrabbit.backend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;

    public NotificationWebSocketHandler() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = Long.valueOf(
                session.getUri().getQuery().split("=")[1]);
        sessions.put(userId, session);
    }

    public void sendToUser(Long userId, Object payload) {
        try {
            WebSocketSession session = sessions.get(userId);
            if (session != null && session.isOpen()) {
                session.sendMessage(
                        new TextMessage(mapper.writeValueAsString(payload)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
