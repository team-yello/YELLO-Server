package com.yello.server.domain.vote.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record CreateVoteRequest(
    List<VoteAnswer> voteAnswerList,
    Integer totalPoint
) {

}
