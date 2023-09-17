package com.yello.server.domain.admin.dto.response;

import com.yello.server.domain.question.entity.Question;
import lombok.Builder;

@Builder
public record AdminQuestionContentVO(
    Long id,
    String nameHead,
    String nameFoot,
    String keywordHead,
    String keywordFoot
) {

    public static AdminQuestionContentVO of(Question question) {

        return AdminQuestionContentVO.builder()
            .id(question.getId())
            .nameHead(question.getNameHead())
            .nameFoot(question.getNameFoot())
            .keywordHead(question.getKeywordHead())
            .keywordFoot(question.getKeywordFoot())
            .build();
    }
}
