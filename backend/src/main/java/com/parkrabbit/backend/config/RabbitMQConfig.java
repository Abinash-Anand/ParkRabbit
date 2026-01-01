package com.parkrabbit.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "reservation.exchange";
    public static final String EXPIRED_QUEUE = "reservation.expired.queue";
    public static final String EXPIRED_ROUTING_KEY = "reservation.expired";

    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue expiredReservationQueue() {
        return QueueBuilder.durable(EXPIRED_QUEUE).build();
    }

    @Bean
    public Binding expiredReservationBinding() {
        return BindingBuilder
                .bind(expiredReservationQueue())
                .to(reservationExchange())
                .with(EXPIRED_ROUTING_KEY);
    }
}
