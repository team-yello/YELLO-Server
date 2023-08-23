package com.yello.server.domain.vote.dto.response;

import static com.yello.server.global.common.factory.TimeFactory.toFormattedString;

import com.yello.server.domain.vote.entity.Vote;
import lombok.Builder;

@Builder
public record VoteResponse(
    Long id,
    String senderGender,
    String senderName,
    Integer nameHint,
    VoteContentVO vote,
    Boolean isHintUsed,
    Boolean isRead,
    String createdAt
) {

    public static VoteResponse of(Vote vote) {
        return VoteResponse.builder()
            .id(vote.getId())
            .senderGender(vote.getSender().getGender().name())
            .senderName(vote.getSender().getName())
            .nameHint(vote.getNameHint())
            .vote(VoteContentVO.of(vote))
            .isHintUsed(vote.getIsAnswerRevealed())
            .isRead(vote.getIsRead())
            .createdAt(toFormattedString(vote.getCreatedAt()))
            .build();
    }
}
