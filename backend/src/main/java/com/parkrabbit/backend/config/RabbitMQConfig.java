package com.parkrabbit.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "reservation.exchange";
    public static final String PARKING_EXCHANGE = "parking.exchange";

    public static final String EXPIRED_QUEUE = "reservation.expired.queue";
    public static final String EXPIRED_ROUTING_KEY = "reservation.expired";

    public static final String SLOT_ASSIGNED_QUEUE = "slot.auto.assigned.queue";
    public static final String SLOT_ASSIGNED_ROUTING_KEY = "slot.auto.assigned";

    public static final String PARKING_SESSION_STARTED_ROUTING_KEY = "parking.session.started";
    public static final String PARKING_SESSION_ENDING_ROUTING_KEY = "parking.session.ending";


    @Bean
    public DirectExchange reservationExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue expiredQueue() {
        return QueueBuilder.durable(EXPIRED_QUEUE).build();
    }

    @Bean
    public Queue slotAutoAssignedQueue() {
        return QueueBuilder.durable(SLOT_ASSIGNED_QUEUE).build();
    }

    @Bean
    public Binding expiredBinding() {
        return BindingBuilder
                .bind(expiredQueue())
                .to(reservationExchange())
                .with(EXPIRED_ROUTING_KEY);
    }

    @Bean
    public Queue parkingSessionStartedQueue() {
        return new Queue("parking.session.started.queue", true);
    }

    @Bean
    public Queue parkingSessionEndingQueue() {
        return new Queue("parking.session.ending.queue", true);
    }

    @Bean
    public Binding slotAssignedBinding() {
        return BindingBuilder
                .bind(slotAutoAssignedQueue())
                .to(reservationExchange())
                .with(SLOT_ASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding parkingSessionStartedBinding() {
        return BindingBuilder
                .bind(parkingSessionStartedQueue())
                .to(parkingExchange())   // ✅ FIX
                .with(PARKING_SESSION_STARTED_ROUTING_KEY);
    }

    @Bean
    public Binding parkingSessionEndingBinding() {
        return BindingBuilder
                .bind(parkingSessionEndingQueue())
                .to(parkingExchange())   // ✅ FIX
                .with(PARKING_SESSION_ENDING_ROUTING_KEY);
    }

    @Bean
    public Queue parkingSessionEndedQueue() {
        return new Queue("parking.session.ended.queue", true);
    }

    @Bean
    public Binding parkingSessionEndedBinding() {
        return BindingBuilder
                .bind(parkingSessionEndedQueue())
                .to(parkingExchange())
                .with("parking.session.ended");
    }



    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public TopicExchange parkingExchange() {
        return new TopicExchange(PARKING_EXCHANGE);
    }

    @Bean
    public Queue userNotificationQueue() {
        return new Queue("user.notification.queue", true);
    }

    @Bean
    public Binding userNotificationBinding() {
        return BindingBuilder
                .bind(userNotificationQueue())
                .to(parkingExchange())
                .with("user.notification");
    }

}
