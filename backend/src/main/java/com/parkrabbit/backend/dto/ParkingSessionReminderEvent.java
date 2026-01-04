package com.parkrabbit.backend.dto;


import java.time.LocalDateTime;


public record ParkingSessionReminderEvent(
        Long userId,
        Long sessionId,
        LocalDateTime endsAt


) {
    @Override
    public Long userId() {
        return userId;
    }

    @Override
    public Long sessionId() {
        return sessionId;
    }

    @Override
    public LocalDateTime endsAt() {
        return endsAt;
    }
}