package com.yello.server.domain.cooldown.response;

import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import java.text.MessageFormat;
import lombok.Builder;

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

    //todo 친구 Yello 조회 픽스 시 수정 필요
    static String toSentence(Vote vote) {
        Question question = vote.getQuestion();
        String keyword = vote.getIsAnswerRevealed() ? vote.getAnswer() : "???";
        return MessageFormat.format("{0} 너{1} {2} {3} {4}", vote.getQuestion().getNameHead(),
            vote.getQuestion().getNameFoot(),
            vote.getQuestion().getKeywordHead(), keyword, vote.getQuestion().getKeywordFoot());
    }

    private static String deleteBracket(String target) {
        if (target.contains("(")) {
            return target.split("[)]")[1];
        }
        return target;
    }
}
