package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.question.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Objects;

@Builder
public record QuestionVO(
        @Schema(description = "투표 질문 id", example = "1")
        Long questionId,

        @Schema(description = "투표 내용 중 이름 앞 부분", example = "나는")
        String nameHead,

        @Schema(description = "투표 내용 중 이름 뒷 부분", example = "랑")
        String nameFoot,

        @Schema(description = "투표 내용 중 키워드 앞 부분", example = "한강에서")
        String keywordHead,

        @Schema(description = "투표 내용 중 키워드 뒷 부분", example = "하고 싶어")
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
