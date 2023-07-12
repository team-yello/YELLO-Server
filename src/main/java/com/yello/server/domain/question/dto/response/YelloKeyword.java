package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.keyword.entity.Keyword;
import lombok.Builder;

public record YelloKeyword(
        String keywordName
) {
    public static YelloKeyword of(Keyword keyword){
        return YelloKeyword.builder()
                .keywordName(keyword.getKeywordName())
                .build();
    }

    @Builder
    public YelloKeyword(String keywordName) {
        this.keywordName = keywordName;
    }
}
