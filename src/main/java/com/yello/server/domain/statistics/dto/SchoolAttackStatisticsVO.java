package com.yello.server.domain.statistics.dto;

import com.yello.server.domain.statistics.entity.StatisticsUserGroup;
import lombok.Builder;

@Builder
public record SchoolAttackStatisticsVO(
    String userGroupName,
    Long userCount,
    Long voteCount,
    Long score,
    Long rankNumber,
    Long prevUserCount,
    Long prevVoteCount,
    Long prevScore,
    Long prevRankNumber
) {

    public static SchoolAttackStatisticsVO of(StatisticsUserGroup statisticsUserGroup) {
        final Long score = statisticsUserGroup.getUserCount() + 2 * statisticsUserGroup.getVoteCount();
        final Long prevScore = statisticsUserGroup.getPrevUserCount() + 2 * statisticsUserGroup.getPrevVoteCount();

        return SchoolAttackStatisticsVO.builder()
            .userGroupName(statisticsUserGroup.getGroupName())
            .userCount(statisticsUserGroup.getUserCount())
            .voteCount(statisticsUserGroup.getVoteCount())
            .score(score)
            .rankNumber(statisticsUserGroup.getRankNumber())
            .prevUserCount(statisticsUserGroup.getPrevUserCount())
            .prevVoteCount(statisticsUserGroup.getVoteCount())
            .prevScore(prevScore)
            .prevRankNumber(statisticsUserGroup.getPrevRankNumber())
            .build();
    }
}
