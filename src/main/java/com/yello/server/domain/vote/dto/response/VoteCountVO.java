package com.yello.server.domain.vote.dto.response;

import lombok.Builder;

@Builder
public record VoteCountVO(
    Integer totalCount,
    Integer openCount,
    Integer openKeywordCount,
    Integer openNameCount,
    Integer openFullNameCount
) {

    public static VoteCountVO of(Integer totalCount, Integer openCount, Integer openKeywordCount,
        Integer openNameCount, Integer openFullNameCount) {
        return VoteCountVO.builder()
            .totalCount(totalCount)
            .openCount(openCount)
            .openKeywordCount(openKeywordCount)
            .openNameCount(openNameCount)
            .openFullNameCount(openFullNameCount)
            .build();
    }

}
