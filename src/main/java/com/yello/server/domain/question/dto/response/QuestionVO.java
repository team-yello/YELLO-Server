package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.question.entity.Question;
import java.util.Objects;
import lombok.Builder;

@Builder
public record QuestionVO(
    Long questionId,
    String nameHead,
    String nameFoot,
    String keywordHead,
    String keywordFoot
) {

    public static QuestionVO of(Question question) {
        return QuestionVO.builder()
            .questionId(Objects.isNull(question.getId()) ? null : question.getId())
            .nameHead(Objects.isNull(question.getNameHead()) ? null : question.getNameHead())
            .nameFoot(Objects.isNull(question.getNameFoot()) ? null : question.getNameFoot())
            .keywordHead(Objects.isNull(question.getKeywordHead()) ? null : question.getKeywordHead())
            .keywordFoot(Objects.isNull(question.getKeywordFoot()) ? null : question.getKeywordFoot())
            .build();
    }
}
