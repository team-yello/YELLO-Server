package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record VoteContentVO(
    @Schema(description = "투표 내용 중 키워드 앞 부분", example = "한강에서")
    String keywordHead,

    @Schema(description = "투표 내용 중 키워드 키워드 부분", example = "수영")
    String keyword,

    @Schema(description = "투표 내용 중 키워드 뒷 부분", example = "하고 싶어")
    String keywordFoot
) {

    static VoteContentVO of(Vote vote) {
        return VoteContentVO.builder()
            .keywordHead(vote.getQuestion().getNameHead())
            .keyword(vote.getIsAnswerRevealed() ? vote.getAnswer() : "")
            .keywordFoot(vote.getQuestion().getNameFoot())
            .build();
    }

    static String toSentence(Vote vote) {
        return vote.getQuestion().getNameHead() + "???" + vote.getQuestion().getNameHead();
    }
}
