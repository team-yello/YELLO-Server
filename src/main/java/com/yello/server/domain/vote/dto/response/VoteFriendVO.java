package com.yello.server.domain.vote.dto.response;

import static com.yello.server.global.common.factory.TimeFactory.toFormattedString;

import com.yello.server.domain.vote.entity.Vote;
import lombok.Builder;

@Builder
public record VoteFriendVO(
    Long id,
    String receiverName,
    String senderGender,
    VoteContentVO vote,
    Boolean isHintUsed,
    String createdAt
) {

    public static VoteFriendVO of(Vote vote) {
        return VoteFriendVO.builder()
            .id(vote.getId())
            .receiverName(vote.getReceiver().getName())
            .senderGender(vote.getSender().getGender().name())
            .vote(VoteContentVO.of(vote))
            .isHintUsed(vote.getIsAnswerRevealed())
            .createdAt(toFormattedString(vote.getCreatedAt()))
            .build();
    }
}
