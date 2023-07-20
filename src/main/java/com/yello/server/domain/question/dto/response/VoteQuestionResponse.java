package com.yello.server.domain.question.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record VoteQuestionResponse(
    VoteContentVO question,
    List<VoteShuffleFriend> friendList,
    List<String> keywordList,
    Integer questionPoint
) {

}
