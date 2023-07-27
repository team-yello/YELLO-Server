package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static com.yello.server.global.common.factory.TimeFactory.toFormattedString;

@Builder
public record VoteResponse(
        @Schema(description = "투표 고유 Id 값")
        Long id,

        @Schema(description = "투표를 보낸 유저의 성별", example = "MALE")
        String senderGender,

        @Schema(description = "투표를 보낸 유저의 이름", example = "권세훈")
        String senderName,

        @Schema(description = "이름 힌트 인덱스", example = "-1")
        Integer nameHint,

        @Schema(description = "투표 내용")
        VoteContentVO vote,

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
                .senderGender(vote.getSender().getGender().name())
                .senderName(vote.getSender().getName())
                .nameHint(vote.getNameHint())
                .vote(VoteContentVO.of(vote))
                .isHintUsed(vote.getIsAnswerRevealed())
                .isRead(vote.getIsRead())
                .createdAt(toFormattedString(vote.getCreatedAt()))
                .build();
    }
}
