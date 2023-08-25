package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.entity.Vote;
import lombok.Builder;

@Builder
public record VoteDetailResponse(
    Integer colorIndex,
    Integer currentPoint,
    Integer nameHint,
    Boolean isAnswerRevealed,
    String senderName,
    String senderGender,
    VoteContentVO vote,
    Integer ticketCount,
    Boolean isSubscribe
) {

    public static VoteDetailResponse of(Vote vote, User user) {
        return VoteDetailResponse.builder()
            .colorIndex(vote.getColorIndex())
            .currentPoint(vote.getReceiver().getPoint())
            .nameHint(vote.getNameHint())
            .isAnswerRevealed(vote.getIsAnswerRevealed())
            .senderName(vote.getSender().getName())
            .senderGender(vote.getSender().getGender().name())
            .vote(VoteContentVO.of(vote))
            .ticketCount(user.getTicketCount())
            .isSubscribe(user.getSubscribe()!=Subscribe.NORMAL)
            .build();
    }
}

