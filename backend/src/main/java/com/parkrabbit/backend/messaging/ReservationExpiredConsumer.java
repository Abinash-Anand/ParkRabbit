package com.parkrabbit.backend.messaging;

import com.parkrabbit.backend.config.RabbitMQConfig;
import com.parkrabbit.backend.dto.ReservationExpiredEvent;
import com.parkrabbit.backend.websocket.NotificationWebSocketHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpiredConsumer {

    private final NotificationWebSocketHandler socketHandler;

    public ReservationExpiredConsumer(NotificationWebSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }
    @RabbitListener(queues = RabbitMQConfig.EXPIRED_QUEUE)
    public void handleReservationExpired(ReservationExpiredEvent event) {

        System.out.println("🔔 RESERVATION EXPIRED EVENT RECEIVED");
        System.out.println("Reservation ID: " + event.getReservationId());
        System.out.println("User ID: " + event.getUserId());
        System.out.println("Parking Lot ID: " + event.getParkingLotId());
        System.out.println("Parking Lot Name: " + event.getParkingLotName());
        System.out.println("Parking Lot Address: " + event.getParkingLotAddress());
        System.out.println("Slot ID: " + event.getSlotId());
        System.out.println("Expired At: " + event.getExpiredAt());

        socketHandler.sendToUser(
                event.getUserId(),
                event
        );
    }
}
