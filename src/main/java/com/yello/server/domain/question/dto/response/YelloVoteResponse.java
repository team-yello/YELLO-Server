package com.yello.server.domain.question.dto.response;

import lombok.Builder;

import java.util.List;

public record YelloVoteResponse(
        YelloQuestion question,
        List<YelloFriend> friendList,
        List<YelloKeyword> keywordList,
        Integer questionPoint
) {

    @Builder
    public YelloVoteResponse(YelloQuestion question, List<YelloFriend> friendList, List<YelloKeyword> keywordList, Integer questionPoint) {
        this.question = question;
        this.friendList = friendList;
        this.keywordList = keywordList;
        this.questionPoint = questionPoint;
    }
}
