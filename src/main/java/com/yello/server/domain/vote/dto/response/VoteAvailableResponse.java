package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.yello.server.global.common.util.ConstantUtil.TIMER_MAX_TIME;
import static com.yello.server.global.common.util.TimeUtil.plusTime;
import static com.yello.server.global.common.util.TimeUtil.toDateFormattedString;

@Builder
public record VoteAvailableResponse(
        @Schema(description = "투표 가능 여부 (true=가능, false=불가능)", example = "false")
        Boolean isPossible,

        @Schema(description = "현재 보유중인 포인트", example = "200")
        Integer point,

        @Schema(description = "마지막 투표 시점")
        String createdAt
) {

    public static VoteAvailableResponse of(User user, Cooldown cooldown) {
        return VoteAvailableResponse.builder()
                .isPossible(cooldown.isPossible())
                .point(user.getPoint())
                .createdAt(toDateFormattedString(cooldown.getCreatedAt()))
                .build();
    }

    public static VoteAvailableResponse of(User user, Boolean isPossible, LocalDateTime localDateTime) {
        return VoteAvailableResponse.builder()
                .isPossible(isPossible)
                .point(user.getPoint())
                .createdAt(toDateFormattedString(plusTime(localDateTime, TIMER_MAX_TIME)))
                .build();
    }
}
