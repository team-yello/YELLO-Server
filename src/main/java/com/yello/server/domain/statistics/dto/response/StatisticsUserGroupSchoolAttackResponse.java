package com.yello.server.domain.statistics.dto.response;

import com.yello.server.domain.statistics.dto.SchoolAttackStatisticsVO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record StatisticsUserGroupSchoolAttackResponse(
    Long pageCount,
    Long totalCount,
    LocalDateTime updatedAt,
    List<SchoolAttackStatisticsVO> statisticsList
) {

}
