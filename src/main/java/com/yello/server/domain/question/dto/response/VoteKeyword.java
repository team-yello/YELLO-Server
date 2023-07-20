package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.keyword.entity.Keyword;
import lombok.Builder;

@Builder
public record VoteKeyword(
    String keywordName
) {

    public static VoteKeyword of(Keyword keyword) {
        return VoteKeyword.builder()
            .keywordName(keyword.getKeywordName())
            .build();
    }
}
