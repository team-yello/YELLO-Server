package com.yello.server.infrastructure.rabbitmq.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import com.yello.server.infrastructure.rabbitmq.dto.response.VoteAvailableQueueResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.Connection;
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

    @Override
    public void deleteMessageByMessageId(String messageId) throws IOException, TimeoutException {
        Connection connection = rabbitTemplate.getConnectionFactory()
            .createConnection();

        try (Channel channel = connection.createChannel(true)) {
            long deliveryTag = getDeliveryTagByMessageId(channel, messageId);

            if (deliveryTag!=-1) {
                channel.basicCancel(String.valueOf(deliveryTag));
            }
        }
    }

    private long getDeliveryTagByMessageId(Channel channel, String messageId) throws IOException {
        GetResponse response = channel.basicGet("vote-available-notification-queue", false);

        while (response!=null) {
            String messageBody = new String(response.getBody(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();

            VoteAvailableQueueResponse voteAvailableQueueResponse = objectMapper.readValue(messageBody,
                VoteAvailableQueueResponse.class);

            if (voteAvailableQueueResponse.messageId().equals(messageId)) {
                return response.getEnvelope().getDeliveryTag();
            }

            response = channel.basicGet("vote-available-notification-queue", false);
        }

        return -1;
    }
}
