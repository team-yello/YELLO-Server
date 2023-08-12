package com.yello.server.infrastructure.rabbitmq.service;

import java.io.IOException;
import org.springframework.amqp.core.Message;

public interface ConsumerService {

    void voteAvailableNotificationConsumer(final Message message) throws IOException;
}
