package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import lombok.Builder;
import lombok.val;

@Builder
public record VoteContentVO(
    String nameHead,
    String nameFoot,
    String keywordHead,
    String keyword,
    String keywordFoot
) {

    public static VoteContentVO of(Vote vote) {
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
        return slashIndex!=-1 ? target.substring(slashIndex + 1) : target;
    }
}
