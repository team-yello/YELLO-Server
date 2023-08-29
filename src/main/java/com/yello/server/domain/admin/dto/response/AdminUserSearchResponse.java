package com.yello.server.domain.admin.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record AdminUserSearchResponse(
    Long pageCount,
    Long totalCount,
    List<AdminUserContentVO> userList
) {

    public static AdminUserSearchResponse of(Long totalCount, List<AdminUserContentVO> userList) {
        return AdminUserSearchResponse.builder()
            .pageCount(totalCount % 10 == 0 ? totalCount / 10 : totalCount / 10 + 1)
            .totalCount(totalCount)
            .userList(userList)
            .build();
    }
}
