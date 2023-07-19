package com.yello.server.domain.cooldown.response;

import static com.yello.server.global.common.util.TimeUtil.toFormattedString;

import com.yello.server.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record VoteFriendResponse(
    @Schema(description = "친구 이름")
    String friendName,

    @Schema(description = "친구 프로필 이미지")
    String friendProfileImage,

    @Schema(description = "투표를 보낸 유저의 성별", example = "MALE")
    String senderGender,

    @Schema(description = "투표의 전체 문장", example = "나는 너랑 한강에서 ??? 하고 싶어")
    String voteSentence,

    @Schema(description = "투표 생성 일자", example = "1시간 전")
    String createdAt
) {

    static VoteFriendResponse of(Vote vote) {
        return VoteFriendResponse.builder()
            .friendName(vote.getReceiver().getName())
            .friendProfileImage(vote.getReceiver().getProfileImage())
            .senderGender(vote.getSender().getGender().name())
            .voteSentence(VoteContentVO.toSentence(vote))
            .createdAt(toFormattedString(vote.getCreatedAt()))
            .build();
    }
}

