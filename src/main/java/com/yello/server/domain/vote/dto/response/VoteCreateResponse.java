package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import java.util.List;
import lombok.Builder;

@Builder
public record VoteCreateResponse(
    Integer point,
    List<Vote> votes
) {

    public static VoteCreateResponse of(Integer point, List<Vote> votes) {
        return VoteCreateResponse.builder()
            .point(point)
            .votes(votes)
            .build();
    }

    public VoteCreateResponse toOnlyPoint() {
        return VoteCreateResponse.builder()
            .point(point)
            .build();
    }
}
