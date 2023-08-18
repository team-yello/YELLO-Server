package com.yello.server.small.global.rabbitmq;

import com.yello.server.infrastructure.rabbitmq.repository.MessageQueueRepository;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.springframework.amqp.core.MessagePostProcessor;

public class FakeMessageQueueRepository implements MessageQueueRepository {

    @Override
    public void convertAndSend(String exchange, String routingKey, Object message,
        MessagePostProcessor messagePostProcessor) {

    }

    @Override
    public void deleteMessageByMessageId(String messageId) throws IOException, TimeoutException {
        System.out.println("Delete message by message id " + messageId);
    }
}
