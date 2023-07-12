package com.yello.server.domain.question.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record YelloVoteResponse(
        YelloQuestion question,
        List<YelloFriend> friendList,
        List<YelloKeyword> keywordList,
        Integer questionPoint
) {

}
