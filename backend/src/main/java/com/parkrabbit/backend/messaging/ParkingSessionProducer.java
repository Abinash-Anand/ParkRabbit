package com.parkrabbit.backend.messaging;


import com.parkrabbit.backend.dto.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
public class ParkingSessionProducer {

    private final RabbitTemplate rabbitTemplate;

    public ParkingSessionProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserNotification(UserNotificationEvent event) {
        rabbitTemplate.convertAndSend(
                "parking.exchange",
                "user.notification",
                event
        );
    }

    public void sendSessionStarted(ParkingSessionStartedEvent event) {
        rabbitTemplate.convertAndSend(
                "parking.exchange",
                "parking.session.started",   // ✅ FIXED
                event
        );
    }

    public void sendSessionReminder(ParkingSessionEndingSoonEvent event) {
        rabbitTemplate.convertAndSend(
                "parking.exchange",
                "parking.session.ending",    // ✅ FIXED
                event
        );
    }

    public void sendSessionEnded(ParkingSessionEndedEvent event) {
        rabbitTemplate.convertAndSend(
                "parking.exchange",
                "parking.session.ended",     // ⚠️ queue missing (see below)
                event
        );
    }
}
