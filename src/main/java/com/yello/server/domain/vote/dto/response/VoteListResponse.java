package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.user.entity.User;
import java.util.List;
import lombok.Builder;

@Builder
public record VoteListResponse(
    Integer totalCount,
    Integer ticketCount,
    Integer openCount,
    Integer openKeywordCount,
    Integer openNameCount,
    Integer openFullNameCount,
    List<VoteResponse> votes
) {

    public static VoteListResponse of(VoteCountVO count, List<VoteResponse> votes, User user) {
        return VoteListResponse.builder()
            .totalCount(count.totalCount())
            .votes(votes)
            .ticketCount(user.getTicketCount())
            .openCount(count.openCount())
            .openKeywordCount(count.openKeywordCount())
            .openNameCount(count.openNameCount())
            .openFullNameCount(count.openFullNameCount())
            .build();
    }
}
