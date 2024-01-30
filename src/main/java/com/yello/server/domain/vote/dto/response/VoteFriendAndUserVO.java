package com.yello.server.domain.vote.dto.response;

import static com.yello.server.global.common.factory.TimeFactory.toFormattedString;

import com.yello.server.domain.vote.entity.Vote;
import lombok.Builder;

@Builder
public record VoteFriendAndUserVO(
    Long id,
    Long senderId,
    String senderName,
    String senderGender,
    String senderYelloId,
    String senderProfileImage,
    Long receiverId,
    String receiverName,
    String receiverGender,
    String receiverYelloId,
    String receiverProfileImage,
    VoteContentVO vote,
    Boolean isHintUsed,
    String createdAt,
    Boolean isUserSenderVote
) {

    public static VoteFriendAndUserVO of(Vote vote, Boolean isUserSenderVote) {
        return VoteFriendAndUserVO.builder()
            .id(vote.getId())
            .senderId(vote.getSender().getId())
            .senderName(vote.getSender().getName())
            .senderGender(vote.getSender().getGender().name())
            .senderYelloId(vote.getSender().getYelloId())
            .senderProfileImage(vote.getSender().getProfileImage())
            .receiverId(vote.getReceiver().getId())
            .receiverGender(vote.getReceiver().getGender().name())
            .receiverName(vote.getReceiver().getName())
            .receiverYelloId(vote.getReceiver().getYelloId())
            .receiverProfileImage(vote.getReceiver().getProfileImage())
            .vote(VoteContentVO.of(vote))
            .isHintUsed(vote.getIsAnswerRevealed())
            .createdAt(toFormattedString(vote.getCreatedAt()))
            .isUserSenderVote(isUserSenderVote)
            .build();
    }
}
