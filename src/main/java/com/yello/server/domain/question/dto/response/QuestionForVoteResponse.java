package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record QuestionForVoteResponse(
    QuestionVO question,
    List<FriendShuffleResponse> friendList,
    List<String> keywordList,
    Integer questionPoint
) {

}
