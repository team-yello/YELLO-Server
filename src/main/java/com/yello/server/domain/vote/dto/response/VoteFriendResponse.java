package com.yello.server.domain.vote.dto.response;

import static com.yello.server.global.common.util.TimeUtil.toFormattedString;

import com.yello.server.domain.vote.entity.Vote;

public record VoteFriendResponse(
    String friendName,
    String friendProfileImage,
    String senderGender,
    String voteSentence,
    String createdAt
) {

    static VoteFriendResponse of(Vote vote) {
        return new VoteFriendResponse(
            vote.getReceiver().getName(),
            vote.getReceiver().getProfileImage(),
            vote.getSender().getGender().intial(),
            VoteContentVO.toSentence(vote),
            toFormattedString(vote.getCreatedAt())
        );
    }
}

