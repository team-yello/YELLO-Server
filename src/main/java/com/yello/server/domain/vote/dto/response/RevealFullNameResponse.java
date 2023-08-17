package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record RevealFullNameResponse(
    String name
) {

    public static RevealFullNameResponse of(User sender) {
        return RevealFullNameResponse.builder()
            .name(sender.getName())
            .build();
    }
}
