package com.yello.server.domain.vote.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record VoteFriendAndUserResponse(
    Long totalCount,
    List<VoteFriendAndUserVO> friendVotes

) {

    public static VoteFriendAndUserResponse of(Long totalCount, List<VoteFriendAndUserVO> friendVotes) {
        return VoteFriendAndUserResponse.builder()
            .totalCount(totalCount)
            .friendVotes(friendVotes)
            .build();
    }
}

