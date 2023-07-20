package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.group.entity.School;
import java.util.List;
import lombok.Builder;

@Builder
public record DepartmentSearchResponse(
    Integer totalCount,
    List<DepartmentInfo> groupList
) {

    public static DepartmentSearchResponse of(int totalCount, List<School> schoolList) {
        List<DepartmentInfo> departmentInfoList = schoolList.stream()
            .map(DepartmentInfo::of)
            .toList();

        return DepartmentSearchResponse.builder()
            .totalCount(totalCount)
            .groupList(departmentInfoList)
            .build();
    }
}
