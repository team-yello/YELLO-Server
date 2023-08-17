package com.yello.server.domain.keyword.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import lombok.Builder;

@Builder
public record KeywordCheckResponse(
    String answer
) {

    public static KeywordCheckResponse of(Vote vote) {
        return KeywordCheckResponse.builder()
            .answer(vote.getAnswer())
            .build();
    }

}
