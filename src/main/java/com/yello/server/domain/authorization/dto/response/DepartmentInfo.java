package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.group.entity.School;
import lombok.Builder;

@Builder
public record DepartmentInfo(
    Long groupId,
    String departmentName
) {

    public static DepartmentInfo of(School school) {
        return DepartmentInfo.builder()
            .groupId(school.getId())
            .departmentName(school.getDepartmentName())
            .build();
    }
}
