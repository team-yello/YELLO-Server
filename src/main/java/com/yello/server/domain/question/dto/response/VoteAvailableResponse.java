package com.yello.server.domain.question.dto.response;

import static com.yello.server.global.common.util.ConstantUtil.TIMER_MAX_TIME;
import static com.yello.server.global.common.util.TimeUtil.plusTime;
import static com.yello.server.global.common.util.TimeUtil.toDateFormattedString;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VoteAvailableResponse(
    Boolean isPossible,
    Integer point,
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
