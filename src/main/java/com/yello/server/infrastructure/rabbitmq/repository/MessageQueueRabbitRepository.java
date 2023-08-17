package com.yello.server.infrastructure.rabbitmq.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageQueueRabbitRepository implements MessageQueueRepository {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void convertAndSend(
        String exchange,
        String routingKey,
        Object message,
        MessagePostProcessor messagePostProcessor
    ) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message, messagePostProcessor);
    }
}
