package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.group.entity.UserGroup;
import lombok.Builder;

@Builder
public record ClassNameSearchResponse(
    Long groupId
) {

    public static ClassNameSearchResponse of(UserGroup userGroup) {
        return ClassNameSearchResponse.builder()
            .groupId(userGroup.getId())
            .build();
    }
}
