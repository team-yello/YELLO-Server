package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.group.entity.School;
import lombok.Builder;

import java.util.List;

@Builder
public record DepartmentSearchResponse(
        Integer totalCount,
        List<DepartmentInfo> groupList
) {
    @Builder
    private record DepartmentInfo(
            Long groupId,
            String departmentName
    ){}

    public static DepartmentSearchResponse of(List<School> schoolList) {
        List<DepartmentInfo> departmentInfoList = schoolList.stream()
                .map(school ->
                        DepartmentInfo.builder()
                                .groupId(school.getId())
                                .departmentName(school.getDepartmentName())
                                .build())
                .toList();
        return DepartmentSearchResponse.builder()
                .totalCount(departmentInfoList.size())
                .groupList(departmentInfoList)
                .build();
    }
}
