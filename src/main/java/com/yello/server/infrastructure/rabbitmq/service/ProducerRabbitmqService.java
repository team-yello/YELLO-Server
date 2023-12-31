package com.yello.server.infrastructure.rabbitmq.service;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.infrastructure.rabbitmq.dto.response.VoteAvailableQueueResponse;
import com.yello.server.infrastructure.rabbitmq.repository.MessageQueueRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProducerRabbitmqService implements ProducerService {

    private final MessageQueueRepository messageQueueRepository;

    @Override
    public void produceVoteAvailableNotification(Cooldown cooldown) {
        LocalDateTime end = cooldown.getCreatedAt().plusMinutes(40);
        final long expiration = Duration.between(cooldown.getCreatedAt(), end).toMillis();

        VoteAvailableQueueResponse voteAvailableQueueResponse = VoteAvailableQueueResponse.builder()
            .receiverId(cooldown.getUser().getId())
            .messageId(cooldown.getMessageId())
            .build();

        try {
            messageQueueRepository.convertAndSend(
                "notification-exchange",
                "push.available",
                voteAvailableQueueResponse,
                message -> {
                    message.getMessageProperties().setHeader("x-delay", expiration);
                    return message;
                }
            );
            log.info("[rabbitmq] Successfully produce message. Cooldown [%s]".formatted(cooldown.getMessageId()));
        } catch (Exception exception) {
            log.error("[rabbitmq] %s".formatted(exception.getMessage()));
        }
    }
}