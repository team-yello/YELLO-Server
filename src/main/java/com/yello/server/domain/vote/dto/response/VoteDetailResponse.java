package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record VoteDetailResponse(
    @Schema(description = "투표 컬러 인덱스")
    Integer colorIndex,

    @Schema(description = "현재 보유중인 포인트")
    Integer currentPoint,

    @Schema(description = "이름 힌트 인덱스"
        + "-1 → 이름 힌트가 아직 밝혀지지 않음"
        + "0 → 첫번째 위치에 이름 힌트가 밝혀짐"
        + "1 → 두번째 위치에 이름 힌트가 밝혀짐")
    Integer nameHint,

    @Schema(description = "키워드 공개 여부")
    Boolean isAnswerRevealed,

    @Schema(description = "투표를 작성한 유저의 이름")
    String senderName,

    @Schema(description = "투표를 보낸 유저의 성별", example = "MALE")
    String senderGender,

    @Schema(description = "투표 내용")
    VoteContentVO vote
) {

    public static VoteDetailResponse of(Vote vote) {
        return VoteDetailResponse.builder()
            .colorIndex(vote.getColorIndex())
            .currentPoint(vote.getReceiver().getPoint())
            .nameHint(vote.getNameHint())
            .isAnswerRevealed(vote.getIsAnswerRevealed())
            .senderName(vote.getSender().getName())
            .senderGender(vote.getSender().getGender().name())
            .vote(VoteContentVO.of(vote))
            .build();
    }
}

