package com.yello.server.domain.vote.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record RevealNameResponse(
    char name,
    Integer nameIndex
) {

    public static RevealNameResponse of(User sender, int randomIndex) {
        return RevealNameResponse.builder()
            .name(sender.getName().charAt(randomIndex))
            .nameIndex(randomIndex)
            .build();
    }
}
