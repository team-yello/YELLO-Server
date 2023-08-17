package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import java.util.List;
import lombok.Builder;

@Builder
public record VoteCreateVO(
    Integer point,
    List<Vote> votes
) {

    public static VoteCreateVO of(Integer point, List<Vote> votes) {
        return VoteCreateVO.builder()
            .point(point)
            .votes(votes)
            .build();
    }
}
