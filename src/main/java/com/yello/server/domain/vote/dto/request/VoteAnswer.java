package com.yello.server.domain.vote.dto.request;

import lombok.Builder;

@Builder
public record VoteAnswer(
    Long friendId,
    Long questionId,
    String keywordName,
    Integer colorIndex
) {

}
