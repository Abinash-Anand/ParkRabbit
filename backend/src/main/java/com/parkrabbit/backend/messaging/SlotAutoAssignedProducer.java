package com.parkrabbit.backend.messaging;

import com.parkrabbit.backend.config.RabbitMQConfig;
import com.parkrabbit.backend.dto.SlotAutoAssignedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SlotAutoAssignedProducer {

    private final RabbitTemplate rabbitTemplate;

    public SlotAutoAssignedProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSlotAutoAssignedEvent(SlotAutoAssignedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.SLOT_ASSIGNED_ROUTING_KEY,
                event
        );

        System.out.println(
                "📤 Published SlotAutoAssignedEvent for userId=" + event.getUserId()
        );
    }
}
