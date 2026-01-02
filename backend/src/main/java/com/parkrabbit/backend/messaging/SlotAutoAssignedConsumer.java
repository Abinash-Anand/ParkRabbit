package com.parkrabbit.backend.messaging;

import com.parkrabbit.backend.config.RabbitMQConfig;
import com.parkrabbit.backend.dto.SlotAutoAssignedEvent;
import com.parkrabbit.backend.websocket.NotificationWebSocketHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class SlotAutoAssignedConsumer {

    private final NotificationWebSocketHandler socketHandler;

    public SlotAutoAssignedConsumer(NotificationWebSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @RabbitListener(queues = RabbitMQConfig.SLOT_ASSIGNED_QUEUE)
    public void consume(SlotAutoAssignedEvent event) {

        System.out.println("🔔 SLOT AUTO ASSIGNED");
        System.out.println("User: " + event.getUserId());
        System.out.println("Parking Lot: " + event.getParkingLotName());
        System.out.println("Parking Name: " + event.getParkingLotAddress());

        socketHandler.sendToUser(
                event.getUserId(),
                event
        );
    }
}
