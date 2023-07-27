package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.friend.dto.response.ShuffledFriendForVoteResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record VoteQuestionResponse(
        QuestionVO question,
        List<ShuffledFriendForVoteResponse> friendList,
        List<String> keywordList,
        Integer questionPoint
) {
}
