package com.yello.server.domain.vote.dto.response;

import static com.yello.server.global.common.factory.TimeFactory.toFormattedString;

import com.yello.server.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record VoteFriendVO(
    @Schema(description = "투표 고유 Id 값")
    Long id,

    @Schema(description = "투표를 받은 유저의 이름", example = "권세훈")
    String receiverName,

    @Schema(description = "투표를 보낸 유저의 성별", example = "MALE")
    String senderGender,

    @Schema(description = "투표의 전체 문장")
    VoteContentVO vote,

    @Schema(description = "힌트 사용 여부")
    Boolean isHintUsed,

    @Schema(description = "투표 생성 일자", example = "1시간 전")
    String createdAt
) {

    public static VoteFriendVO of(Vote vote) {
        return VoteFriendVO.builder()
            .id(vote.getId())
            .receiverName(vote.getReceiver().getName())
            .senderGender(vote.getSender().getGender().name())
            .vote(VoteContentVO.of(vote))
            .isHintUsed(vote.getIsAnswerRevealed())
            .createdAt(toFormattedString(vote.getCreatedAt()))
            .build();
    }
}