package com.yello.server.infrastructure.rabbitmq.service;

import com.yello.server.domain.cooldown.entity.Cooldown;

public interface ProducerService {

    void produceVoteAvailableNotification(Cooldown cooldown);

}
