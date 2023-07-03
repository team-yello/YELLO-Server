package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;

public record VoteDetailResponse(
    Integer nameHint,
    Boolean isAnswerRevealed,
    String senderName,
    VoteContentVO vote
) {

    public static VoteDetailResponse of(Vote vote) {
        return new VoteDetailResponse(
            vote.getNameHint(),
            vote.getIsAnswerRevealed(),
            vote.getSender().getName(),
            VoteContentVO.of(vote)
        );
    }
}

