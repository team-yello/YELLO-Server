package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.group.entity.UserGroup;
import java.util.List;
import lombok.Builder;

@Builder
public record DepartmentSearchResponse(
    Integer totalCount,
    List<DepartmentInfo> groupList
) {

    public static DepartmentSearchResponse of(int totalCount, List<UserGroup> userGroupList) {
        List<DepartmentInfo> departmentInfoList = userGroupList.stream()
            .map(DepartmentInfo::of)
            .toList();

        return DepartmentSearchResponse.builder()
            .totalCount(totalCount)
            .groupList(departmentInfoList)
            .build();
    }
}
