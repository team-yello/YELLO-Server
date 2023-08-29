package com.yello.server.domain.admin.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record AdminCooldownResponse(
    Long pageCount,
    Long totalCount,
    List<AdminCooldownContentVO> userList
) {

    public static AdminCooldownResponse of(Long totalCount, List<AdminCooldownContentVO> cooldownList) {
        return AdminCooldownResponse.builder()
            .pageCount(totalCount % 10 == 0 ? totalCount / 10 : totalCount / 10 + 1)
            .totalCount(totalCount)
            .userList(cooldownList)
            .build();
    }
}
