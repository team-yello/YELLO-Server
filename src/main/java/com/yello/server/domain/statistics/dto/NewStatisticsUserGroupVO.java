package com.yello.server.domain.statistics.dto;

public record NewStatisticsUserGroupVO(
    String userGroupName,
    Long userCount,
    Long voteCount,
    Long rankNumber
) {

}
