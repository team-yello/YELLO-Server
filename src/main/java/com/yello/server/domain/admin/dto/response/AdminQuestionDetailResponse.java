package com.yello.server.domain.admin.dto.response;

import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.question.entity.Question;
import java.util.List;
import lombok.Builder;

@Builder
public record AdminQuestionDetailResponse(
    Long id,
    String nameHead,
    String nameFoot,
    String keywordHead,
    String keywordFoot,
    List<String> keywordList
) {

    public static AdminQuestionDetailResponse of(Question question) {
        return AdminQuestionDetailResponse.builder()
            .id(question.getId())
            .nameHead(question.getNameHead())
            .nameFoot(question.getNameFoot())
            .keywordHead(question.getKeywordHead())
            .keywordFoot(question.getKeywordFoot())
            .keywordList(question.getKeywordList()
                .stream()
                .map((Keyword::getKeywordName))
                .toList())
            .build();
    }
}
