package com.yello.server.domain.vote.dto.response;

import static com.yello.server.global.common.util.TimeUtil.toFormattedString;

import com.yello.server.domain.vote.entity.Vote;

public record VoteResponse(
    Long id,
    String gender,
    Boolean isHintUsed,
    Boolean isRead,
    String createdAt
) {

    public static VoteResponse of(Vote vote) {
        return new VoteResponse(
            vote.getId(),
            vote.getSender().getGender().intial(),
            vote.getIsAnswerRevealed(),
            vote.getIsRead(),
            toFormattedString(vote.getCreatedAt())
        );
    }
}
