package com.parkrabbit.backend.dto;


import java.time.LocalDateTime;

public class ParkingSessionEndedEvent {

    private Long userId;
    private Long sessionId;
    private LocalDateTime endedAt;

    public ParkingSessionEndedEvent(Long userId, Long sessionId, LocalDateTime endedAt) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.endedAt = endedAt;
    }

    public Long getUserId() { return userId; }
    public Long getSessionId() { return sessionId; }
    public LocalDateTime getEndedAt() { return endedAt; }
}
