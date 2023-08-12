package com.yello.server.infrastructure.rabbitmq.service;

import com.yello.server.domain.vote.entity.Vote;

public interface ProducerService {

    void produceVoteAvailableNotification(Vote vote);

}
