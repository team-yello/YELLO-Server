package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record QuestionForVoteResponse(
        QuestionVO question,
        List<FriendShuffleResponse> friendList,
        List<String> keywordList,
        Integer questionPoint
) {
}
