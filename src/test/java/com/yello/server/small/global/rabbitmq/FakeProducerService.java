package com.yello.server.small.global.rabbitmq;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.infrastructure.rabbitmq.repository.MessageQueueRepository;
import com.yello.server.infrastructure.rabbitmq.service.ProducerService;

public class FakeProducerService implements ProducerService {

    private final MessageQueueRepository messageQueueRepository;

    public FakeProducerService(MessageQueueRepository messageQueueRepository) {
        this.messageQueueRepository = messageQueueRepository;
    }

    @Override
    public void produceVoteAvailableNotification(Cooldown cooldown) {

    }
}
