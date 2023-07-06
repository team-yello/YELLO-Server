package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record VoteContentVO(
    @Schema(description = "투표 내용 중 앞 부분", example = "나는 너랑 한강에서")
    String head,

    @Schema(description = "투표 내용 중 키워드 부분", example = "수영")
    String answer,

    @Schema(description = "투표 내용 중 뒷 부분", example = "하고 싶어")
    String foot
) {

    static VoteContentVO of(Vote vote) {
        return VoteContentVO.builder()
            .head(vote.getQuestion().getNameHead())
            .answer(vote.getIsAnswerRevealed() ? vote.getAnswer() : "")
            .foot(vote.getQuestion().getNameFoot())
            .build();
    }

    static String toSentence(Vote vote) {
        return vote.getQuestion().getNameHead() + "???" + vote.getQuestion().getNameHead();
    }
}
