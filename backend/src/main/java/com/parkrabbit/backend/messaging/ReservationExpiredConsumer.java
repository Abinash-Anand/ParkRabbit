package com.parkrabbit.backend.messaging;

import com.parkrabbit.backend.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpiredConsumer {

    @RabbitListener(queues = RabbitMQConfig.EXPIRED_QUEUE)
    public void handleExpiredReservation(Long reservationId) {
        System.out.println(
                "🔥 Reservation expired event received for ID = " + reservationId
        );
    }
}
