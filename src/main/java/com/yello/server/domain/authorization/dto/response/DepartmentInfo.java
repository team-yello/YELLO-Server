package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.group.entity.UserGroup;
import lombok.Builder;

@Builder
public record DepartmentInfo(
    Long groupId,
    String departmentName
) {

    public static DepartmentInfo of(UserGroup userGroup) {
        return DepartmentInfo.builder()
            .groupId(userGroup.getId())
            .departmentName(userGroup.getSubGroupName())
            .build();
    }
}
