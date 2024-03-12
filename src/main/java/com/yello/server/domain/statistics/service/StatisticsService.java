package com.yello.server.domain.statistics.service;

import com.yello.server.domain.statistics.dto.NewStatisticsUserGroupVO;
import com.yello.server.domain.statistics.dto.SchoolAttackStatisticsVO;
import com.yello.server.domain.statistics.dto.response.StatisticsUserGroupSchoolAttackResponse;
import com.yello.server.domain.statistics.entity.StatisticsUserGroup;
import com.yello.server.domain.statistics.repository.StatisticsRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    @Transactional
    public void writeUserGroupStatistics() {
        final LocalDateTime voteStartAt = LocalDateTime.of(2023, 12, 1, 0, 0, 0);
        final List<NewStatisticsUserGroupVO> newStatistics = statisticsRepository.getUserGroupNewStatistics(
            voteStartAt);

        for (NewStatisticsUserGroupVO newStatistic : newStatistics) {
            final Optional<StatisticsUserGroup> groupStatistics = statisticsRepository.findByUserGroupName(
                newStatistic.userGroupName());

            if (groupStatistics.isEmpty()) {
                statisticsRepository.save(StatisticsUserGroup.builder()
                    .groupName(newStatistic.userGroupName())
                    .userCount(newStatistic.userCount())
                    .voteCount(newStatistic.voteCount())
                    .alpha(0L)
                    .rankNumber(newStatistic.rankNumber())
                    .prevUserCount(0L)
                    .prevVoteCount(0L)
                    .prevAlpha(0L)
                    .prevRankNumber(0L)
                    .build());
            } else {
                groupStatistics.get().update(
                    newStatistic.voteCount(),
                    newStatistic.userCount(),
                    0L,
                    newStatistic.rankNumber(),
                    groupStatistics.get().getVoteCount(),
                    groupStatistics.get().getUserCount(),
                    groupStatistics.get().getAlpha(),
                    groupStatistics.get().getRankNumber()
                );
            }
        }
    }

    public StatisticsUserGroupSchoolAttackResponse getSchoolAttackStatistics(Pageable pageable) {
        final List<StatisticsUserGroup> statistics = statisticsRepository.getSchoolAttackStatistics(
            pageable);
        final Long counted = statisticsRepository.countSchoolAttackStatistics();
        final LocalDateTime lastUpdatedAt = statisticsRepository.getSchoolAttackLastUpdatedAt();
        long pageSize = pageable.getPageSize();

        return StatisticsUserGroupSchoolAttackResponse.builder()
            .pageCount(counted % pageSize == 0 ? counted / pageSize : counted / pageSize + 1)
            .totalCount(counted)
            .updatedAt(lastUpdatedAt)
            .statisticsList(statistics.stream().map(SchoolAttackStatisticsVO::of).toList())
            .build();
    }

    public SchoolAttackStatisticsVO getSchoolAttackStatisticsByGroupName(String groupName) {
        return SchoolAttackStatisticsVO.of(
            statisticsRepository.getByUserGroupName(groupName)
        );
    }

    public StatisticsUserGroupSchoolAttackResponse getSchoolAttackStatisticsLikeGroupName(String groupName,
        Pageable pageable) {
        final List<StatisticsUserGroup> statistics = statisticsRepository.getSchoolAttackStatisticsContaining(
            groupName, pageable);
        final Long counted = statisticsRepository.countSchoolAttackStatisticsContaining(groupName);
        final LocalDateTime lastUpdatedAt = statisticsRepository.getSchoolAttackLastUpdatedAt();
        long pageSize = pageable.getPageSize();

        return StatisticsUserGroupSchoolAttackResponse.builder()
            .pageCount(counted % pageSize == 0 ? counted / pageSize : counted / pageSize + 1)
            .totalCount(counted)
            .updatedAt(lastUpdatedAt)
            .statisticsList(statistics.stream().map(SchoolAttackStatisticsVO::of).toList())
            .build();
    }
}