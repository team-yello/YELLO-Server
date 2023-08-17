package com.yello.server.infrastructure.rabbitmq.repository;

import org.springframework.amqp.core.MessagePostProcessor;

public interface MessageQueueRepository {

    void convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor);

}
