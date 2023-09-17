package com.yello.server.domain.admin.dto.request;

import lombok.Builder;

@Builder
public record AdminQuestionVoteContentVO(
    Long senderId,
    Long receiverId,
    String keyword,
    Integer colorIndex
) {

}
