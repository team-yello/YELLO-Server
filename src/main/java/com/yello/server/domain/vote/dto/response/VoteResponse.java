package com.yello.server.domain.vote.dto.response;

import static com.yello.server.global.common.util.TimeUtil.toFormattedString;

import com.yello.server.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record VoteResponse(
    @Schema(description = "투표 고유 Id 값")
    Long id,

    @Schema(description = "투표를 보낸 유저의 성별", example = "MALE")
    String gender,

    @Schema(description = "힌트 사용 여부")
    Boolean isHintUsed,

    @Schema(description = "투표 읽음 여부")
    Boolean isRead,

    @Schema(description = "투표 생성 일자", example = "1시간 전")
    String createdAt
) {

    public static VoteResponse of(Vote vote) {
        return VoteResponse.builder()
            .id(vote.getId())
            .gender(vote.getSender().getGender().name())
            .isHintUsed(vote.getIsAnswerRevealed())
            .isRead(vote.getIsRead())
            .createdAt(toFormattedString(vote.getCreatedAt()))
            .build();
    }
}
