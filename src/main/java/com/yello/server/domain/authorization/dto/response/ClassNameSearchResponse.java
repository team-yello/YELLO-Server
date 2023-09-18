package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.group.entity.School;
import lombok.Builder;

@Builder
public record ClassNameSearchResponse(
    Long groupId
) {

    public static ClassNameSearchResponse of(School school) {
        return ClassNameSearchResponse.builder()
            .groupId(school.getId())
            .build();
    }
}
