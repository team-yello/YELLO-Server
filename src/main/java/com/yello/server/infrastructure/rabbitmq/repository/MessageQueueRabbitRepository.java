package com.yello.server.infrastructure.rabbitmq.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
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
