package com.yello.server.domain.keyword.dto.response;

import com.yello.server.domain.keyword.entity.Keyword;
import lombok.Builder;

@Builder
public record KeywordVO(
    String keywordName
) {

    public static KeywordVO of(Keyword keyword) {
        return KeywordVO.builder()
            .keywordName(keyword.getKeywordName())
            .build();
    }
}
