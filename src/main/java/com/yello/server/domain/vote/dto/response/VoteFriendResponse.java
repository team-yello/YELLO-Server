package com.yello.server.domain.vote.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record VoteFriendResponse(
    Integer totalCount,
    List<VoteFriendVO> friendVotes

) {

    public static VoteFriendResponse of(Integer totalCount, List<VoteFriendVO> friendVotes) {
        return VoteFriendResponse.builder()
            .totalCount(totalCount)
            .friendVotes(friendVotes)
            .build();
    }
}

