package com.yello.server.domain.question.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record VoteQuestionResponse(
        VoteContentVO question,
        List<VoteShuffleFriend> friendList,
        List<String> keywordList,
        Integer questionPoint
) {

}
