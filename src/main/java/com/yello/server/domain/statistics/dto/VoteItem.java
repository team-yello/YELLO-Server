package com.yello.server.domain.statistics.dto;

import com.yello.server.domain.group.entity.UserGroupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VoteItem {

    UserGroupType groupType;
    int yearInfo;
    Long totalCount;
}
