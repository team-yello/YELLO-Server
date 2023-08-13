package com.yello.server.infrastructure.rabbitmq.service;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.infrastructure.rabbitmq.dto.response.VoteAvailableQueueResponse;
import com.yello.server.infrastructure.rabbitmq.repository.MessageQueueRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerRabbitmqService implements ProducerService {

    private final MessageQueueRepository messageQueueRepository;

    @Override
    public void produceVoteAvailableNotification(Cooldown cooldown) {
        LocalDateTime notificationTime = cooldown.getCreatedAt().plusMinutes(1);
        String expiration = String.valueOf(Duration.between(LocalDateTime.now(), notificationTime).toMillis());

        VoteAvailableQueueResponse voteAvailableQueueResponse = VoteAvailableQueueResponse.builder()
            .receiverId(cooldown.getUser().getId())
            .build();

        messageQueueRepository.convertAndSend(
            "notification-exchange",
            "push.available",
            voteAvailableQueueResponse,
            message -> {
                message.getMessageProperties()
                    .setExpiration(expiration);
                return message;
            }
        );
    }
}