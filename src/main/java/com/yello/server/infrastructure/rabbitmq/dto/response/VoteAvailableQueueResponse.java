package com.yello.server.infrastructure.rabbitmq.dto.response;

import lombok.Builder;

@Builder
public record VoteAvailableQueueResponse(
    Long receiverId,
    String messageId
) {

}
