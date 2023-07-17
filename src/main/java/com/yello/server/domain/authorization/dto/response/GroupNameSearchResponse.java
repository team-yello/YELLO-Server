package com.yello.server.domain.authorization.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GroupNameSearchResponse(
        Integer totalCount,
        List<String> groupNameList
) {
    public static GroupNameSearchResponse of(int totalCount, List<String> list) {
        return GroupNameSearchResponse.builder()
                .totalCount(totalCount)
                .groupNameList(list)
                .build();
    }
}
