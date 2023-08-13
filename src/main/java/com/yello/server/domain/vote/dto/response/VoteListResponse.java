package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.user.entity.User;
import java.util.List;
import lombok.Builder;

@Builder
public record VoteListResponse(
    Integer totalCount,
    Integer ticketCount,
    List<VoteResponse> votes
) {

    public static VoteListResponse of(Integer totalCount, List<VoteResponse> votes, User user) {
        return VoteListResponse.builder()
            .totalCount(totalCount)
            .votes(votes)
            .ticketCount(user.getTicketCount())
            .build();
    }
}
