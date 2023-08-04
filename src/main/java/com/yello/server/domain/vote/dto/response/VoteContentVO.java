package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.val;

@Builder
public record VoteContentVO(
    @Schema(description = "투표 내용 중 이름 앞 부분", example = "나는")
    String nameHead,

    @Schema(description = "투표 내용 중 이름 뒷 부분", example = "랑")
    String nameFoot,

    @Schema(description = "투표 내용 중 키워드 앞 부분", example = "한강에서")
    String keywordHead,

    @Schema(description = "투표 내용 중 키워드 키워드 부분", example = "수영")
    String keyword,

    @Schema(description = "투표 내용 중 키워드 뒷 부분", example = "하고 싶어")
    String keywordFoot
) {

    static VoteContentVO of(Vote vote) {
        return VoteContentVO.builder()
            .nameHead(vote.getQuestion().getNameHead())
            .nameFoot(deleteBracket(vote.getQuestion().getNameFoot()))
            .keywordHead(vote.getQuestion().getKeywordHead())
            .keyword(vote.getAnswer())
            .keywordFoot(vote.getQuestion().getKeywordFoot())
            .build();
    }

    private static String deleteBracket(String target) {
        val slashIndex = target.indexOf('/');
        return slashIndex != -1 ? target.substring(slashIndex + 1) : target;
    }
}
