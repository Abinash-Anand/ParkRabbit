package com.parkrabbit.backend.dto;

import java.time.LocalDateTime;

public class UserNotificationEvent {

    private Long userId;
    private String type;       // SESSION_STARTED, SESSION_REMINDER, SESSION_ENDED, SLOT_ASSIGNED
    private String message;
    private LocalDateTime timestamp;

    public UserNotificationEvent(
            Long userId,
            String type,
            String message
    ) {
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public Long getUserId() { return userId; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
