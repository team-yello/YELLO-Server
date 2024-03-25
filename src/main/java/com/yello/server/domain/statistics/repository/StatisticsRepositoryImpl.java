package com.yello.server.domain.statistics.repository;

import static com.yello.server.global.common.ErrorCode.STATISTICS_NOT_FOUND_EXCEPTION;

import com.yello.server.domain.statistics.dto.NewStatisticsUserGroupVO;
import com.yello.server.domain.statistics.entity.StatisticsDaily;
import com.yello.server.domain.statistics.entity.StatisticsUserGroup;
import com.yello.server.domain.statistics.exception.StatisticsNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final StatisticsDailyJpaRepository statisticsDailyJpaRepository;
    private final StatisticsUserGroupJpaRepository userGroupJpaRepository;

    @Override
    public Optional<StatisticsUserGroup> findByUserGroupName(String userGroupName) {
        return userGroupJpaRepository.findByGroupName(userGroupName);
    }

    @Override
    public StatisticsUserGroup getByUserGroupName(String groupName) {
        return userGroupJpaRepository.findByGroupName(groupName)
            .orElseThrow(() -> new StatisticsNotFoundException(STATISTICS_NOT_FOUND_EXCEPTION));
    }

    @Override
    public StatisticsUserGroup save(StatisticsUserGroup statistics) {
        return userGroupJpaRepository.save(statistics);
    }

    @Override
    public List<NewStatisticsUserGroupVO> getUserGroupNewStatistics(LocalDateTime voteStartAt) {
        return userGroupJpaRepository.getNewUserGroupStatistics(voteStartAt);
    }

    @Override
    public Long countSchoolAttackStatistics() {
        return userGroupJpaRepository.countSchoolAttackStatistics();
    }

    @Override
    public Long countSchoolAttackStatisticsContaining(String groupName) {
        return userGroupJpaRepository.countSchoolAttackStatisticsContaining(groupName);
    }

    @Override
    public LocalDateTime getSchoolAttackLastUpdatedAt() {
        return userGroupJpaRepository.getSchoolAttackLastUpdatedAt();
    }

    @Override
    public List<StatisticsUserGroup> getSchoolAttackStatistics(Pageable pageable) {
        return userGroupJpaRepository.getSchoolAttackStatistics(pageable);
    }

    @Override
    public List<StatisticsUserGroup> getSchoolAttackStatisticsContaining(String groupName, Pageable pageable) {
        return userGroupJpaRepository.getSchoolAttackStatisticsContaining(groupName, pageable);
    }

    @Override
    public StatisticsDaily save(StatisticsDaily newStatisticsDaily) {
        return statisticsDailyJpaRepository.save(newStatisticsDaily);
    }

    @Override
    public StatisticsDaily getById(Long id) {
        return statisticsDailyJpaRepository.findById(id)
            .orElseThrow(() -> new StatisticsNotFoundException(STATISTICS_NOT_FOUND_EXCEPTION));
    }
}
