package com.yello.server.domain.admin.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record AdminQuestionVoteRequest(
    List<AdminQuestionVoteContentVO> voteContentList
) {

}
