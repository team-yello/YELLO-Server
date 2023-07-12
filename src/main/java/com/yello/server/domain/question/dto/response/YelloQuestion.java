package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.question.entity.Question;
import lombok.Builder;

import java.util.Objects;

@Builder
public record YelloQuestion(
        String nameHead,
        String nameFoot,
        String keywordHead,
        String keywordFoot
) {
    public static YelloQuestion of(Question question) {
        return YelloQuestion.builder()
                .nameHead(Objects.isNull(question.getNameHead()) ? null : question.getNameHead())
                .nameFoot(Objects.isNull(question.getNameFoot()) ? null : question.getNameFoot())
                .keywordHead(Objects.isNull(question.getKeywordHead()) ? null : question.getKeywordHead())
                .keywordFoot(Objects.isNull(question.getKeywordFoot()) ? null : question.getKeywordFoot())
                .build();
    }


}
