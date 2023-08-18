package com.yello.server.infrastructure.rabbitmq.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import com.yello.server.infrastructure.rabbitmq.dto.response.VoteAvailableQueueResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.Connection;
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

    @Override
    public void deleteMessageByMessageId(String messageId) throws IOException, TimeoutException {
        Connection connection = rabbitTemplate.getConnectionFactory()
            .createConnection();

        log.info("[rabbitmq] connected %s".formatted(connection.toString()));

        try (Channel channel = connection.createChannel(true)) {
            log.info("[rabbitmq] channel is %s".formatted(channel.toString()));
            long deliveryTag = getDeliveryTagByMessageId(channel, messageId);

            log.info("[rabbitmq] response = " + channel.basicGet("vote-available-notification-queue", false));
            if (deliveryTag!=-1) {
                channel.basicAck(deliveryTag, false);
                log.info("[rabbitmq] Successfully delete message %d !".formatted(deliveryTag));
            }
        }
    }

    private long getDeliveryTagByMessageId(Channel channel, String messageId) throws IOException {
        GetResponse response = channel.basicGet("vote-available-notification-queue", false);
        log.info("[rabbitmq] Successfully get channel %s".formatted(response.getEnvelope().getExchange()));

        while (response!=null) {
            log.info("[rabbitmq] Searching message for remove ...");
            String messageBody = new String(response.getBody(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();

            VoteAvailableQueueResponse voteAvailableQueueResponse = objectMapper.readValue(messageBody,
                VoteAvailableQueueResponse.class);

            if (voteAvailableQueueResponse.messageId().equals(messageId)) {
                log.info("[rabbitmq] find message: %d".formatted(response.getEnvelope().getDeliveryTag()));
                return response.getEnvelope().getDeliveryTag();
            }

            response = channel.basicGet("vote-available-notification-queue", false);
        }

        return -1;
    }
}
