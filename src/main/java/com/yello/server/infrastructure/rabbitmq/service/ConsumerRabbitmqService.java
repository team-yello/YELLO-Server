package com.yello.server.infrastructure.rabbitmq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import com.yello.server.infrastructure.rabbitmq.dto.response.VoteAvailableQueueResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ConsumerRabbitmqService implements ConsumerService {

    private final CooldownRepository cooldownRepository;

    private final NotificationService notificationService;

    @Override
    @RabbitListener(queues = "vote-available-notification-queue", concurrency = "6")
    public void consumeVoteAvailableNotification(final Message message) throws IOException {
        log.info("[rabbitmq] start consume message. [%s]".formatted(message.getMessageProperties().getMessageId()));
        ObjectMapper objectMapper = new ObjectMapper();

        final VoteAvailableQueueResponse voteAvailableQueueResponse = objectMapper.readValue(
            message.getBody(),
            VoteAvailableQueueResponse.class
        );

        try {
            boolean exists = cooldownRepository.existsByMessageId(voteAvailableQueueResponse.messageId());
            if (exists) {
                notificationService.sendVoteAvailableNotification(voteAvailableQueueResponse.receiverId());
                log.info("[rabbitmq] Successfully consume message %s".formatted(
                    voteAvailableQueueResponse.messageId())
                );
            } else {
                log.info("[rabbitmq] Already removed message %s".formatted(
                    voteAvailableQueueResponse.messageId())
                );
            }
        } catch (Exception exception) {
            log.error("[rabbitmq] %s".formatted(exception.getMessage()));
        }
    }
}

