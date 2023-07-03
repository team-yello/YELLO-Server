package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;

record VoteContentVO(
    String head,
    String answer,
    String foot
) {

    static VoteContentVO of(Vote vote) {
        return new VoteContentVO(
            vote.getHead(),
            vote.getIsAnswerRevealed() ? vote.getAnswer() : "",
            vote.getFoot());
    }

    static String toSentence(Vote vote) {
        return vote.getHead() + "???" + vote.getFoot();
    }
}
