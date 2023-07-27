package com.yello.server.domain.vote.dto.request;

import java.util.List;

public record CreateVoteRequest(
        List<VoteAnswer> voteAnswerList,
        Integer totalPoint
) {

}
