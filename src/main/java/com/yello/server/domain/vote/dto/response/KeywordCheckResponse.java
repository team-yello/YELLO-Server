package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import lombok.Builder;

public record KeywordCheckResponse(
        String answer
) {
    public static KeywordCheckResponse of(Vote vote){
        return KeywordCheckResponse.builder()
                .answer(vote.getAnswer())
                .build();
    }

    @Builder
    public KeywordCheckResponse(String answer) {
        this.answer = answer;
    }
}
