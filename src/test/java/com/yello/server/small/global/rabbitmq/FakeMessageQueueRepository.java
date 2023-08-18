package com.yello.server.small.global.rabbitmq;

import com.yello.server.infrastructure.rabbitmq.repository.MessageQueueRepository;
import org.springframework.amqp.core.MessagePostProcessor;

public class FakeMessageQueueRepository implements MessageQueueRepository {

    @Override
    public void convertAndSend(String exchange, String routingKey, Object message,
        MessagePostProcessor messagePostProcessor) {

    }
}
