package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

public record YelloStartResponse(
        Boolean isStart,
        Integer point,
        String createdAt
){

    @Builder
    public YelloStartResponse(Boolean isStart, Integer point, String createdAt) {
        this.isStart = isStart;
        this.point = point;
        this.createdAt = createdAt;
    }
}
