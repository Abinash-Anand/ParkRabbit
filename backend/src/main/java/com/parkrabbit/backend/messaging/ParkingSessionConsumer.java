package com.parkrabbit.backend.messaging;

import com.parkrabbit.backend.dto.*;
import com.parkrabbit.backend.websocket.NotificationWebSocketHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ParkingSessionConsumer {

    private final NotificationWebSocketHandler socketHandler;

    public ParkingSessionConsumer(NotificationWebSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @RabbitListener(queues = "parking.session.started.queue")
    public void handleStarted(ParkingSessionStartedEvent event) {
        System.out.println("📢 SESSION STARTED: " + event);
        socketHandler.sendToUser(event.getUserId(), event);
    }

    @RabbitListener(queues = "parking.session.ending.queue")
    public void handleEndingSoon(ParkingSessionEndingSoonEvent event) {
        System.out.println("⏰ SESSION ENDING SOON: " + event);
        socketHandler.sendToUser(event.getUserId(), event);
    }

    @RabbitListener(queues = "parking.session.ended.queue")
    public void handleEnded(ParkingSessionEndedEvent event) {
        System.out.println("🛑 SESSION ENDED: " + event);
        socketHandler.sendToUser(event.getUserId(), event);
    }

    @RabbitListener(queues = "user.notification.queue")
    public void handleUserNotification(UserNotificationEvent event) {

        // 👨‍💻 Backend console
        System.out.println(
                "🔔 NOTIFICATION [" + event.getType() + "] → User "
                        + event.getUserId() + " : " + event.getMessage()
        );

        // 🌐 WebSocket → frontend
        socketHandler.sendToUser(event.getUserId(), event);
    }
}
