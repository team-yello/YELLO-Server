package com.yello.server.infrastructure.rabbitmq.repository;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.springframework.amqp.core.MessagePostProcessor;

public interface MessageQueueRepository {

    void convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor);

    void deleteMessageByMessageId(String messageId) throws IOException, TimeoutException;

}
