package com.yello.server.domain.statistics.repository;

import com.yello.server.domain.statistics.dto.NewStatisticsUserGroupVO;
import com.yello.server.domain.statistics.entity.StatisticsDaily;
import com.yello.server.domain.statistics.entity.StatisticsUserGroup;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface StatisticsRepository {

    Optional<StatisticsUserGroup> findByUserGroupName(String userGroupName);

    StatisticsUserGroup getByUserGroupName(String groupName);

    StatisticsUserGroup save(StatisticsUserGroup statistics);

    List<NewStatisticsUserGroupVO> getUserGroupNewStatistics(LocalDateTime voteStartAt);

    Long countSchoolAttackStatistics();

    Long countSchoolAttackStatisticsContaining(String groupName);

    LocalDateTime getSchoolAttackLastUpdatedAt();

    List<StatisticsUserGroup> getSchoolAttackStatistics(Pageable pageable);

    List<StatisticsUserGroup> getSchoolAttackStatisticsContaining(String groupName, Pageable pageable);

    StatisticsDaily save(StatisticsDaily newStatisticsDaily);

    StatisticsDaily getById(Long id);
}
