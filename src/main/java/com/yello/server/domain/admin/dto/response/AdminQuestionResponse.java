package com.yello.server.domain.admin.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record AdminQuestionResponse(
    Long pageCount,
    Long totalCount,
    List<AdminQuestionContentVO> questionList
) {

    public static AdminQuestionResponse of(Long totalCount, List<AdminQuestionContentVO> questionList) {
        return AdminQuestionResponse.builder()
            .pageCount(totalCount % 20 == 0 ? totalCount / 20 : totalCount / 20 + 1)
            .totalCount(totalCount)
            .questionList(questionList)
            .build();
    }
}
