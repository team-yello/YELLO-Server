package com.yello.server.domain.authorization.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record GroupNameSearchResponse(
    Integer totalCount,
    List<String> groupNameList
) {

    public static GroupNameSearchResponse of(int totalCount, List<String> groupNameList) {
        return GroupNameSearchResponse.builder()
            .totalCount(totalCount)
            .groupNameList(groupNameList)
            .build();
    }
}
