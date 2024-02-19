package com.yello.server.domain.statistics.repository;

import com.yello.server.domain.statistics.dto.NewStatisticsUserGroupVO;
import com.yello.server.domain.statistics.entity.StatisticsUserGroup;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatisticsUserGroupJpaRepository extends JpaRepository<StatisticsUserGroup, Long> {

    Optional<StatisticsUserGroup> findByGroupName(String groupName);

    @Query("SELECT "
        + "    new com.yello.server.domain.statistics.dto.NewStatisticsUserGroupVO( "
        + "    userGroup.groupName, "
        + "    COALESCE(user_count_table.user_count, 0), "
        + "    COALESCE(vote_count_table.vote_count, 0),"
        + "    RANK() OVER (ORDER BY COALESCE(user_count_table.user_count, 0) + COALESCE(vote_count_table.vote_count, 0) * 2 DESC) ) "
        + "FROM  "
        + "    UserGroup userGroup "
        + "LEFT JOIN "
        + "    (SELECT "
        + "        ug.groupName AS group_name, "
        + "        COUNT(user.id) AS user_count "
        + "    FROM "
        + "        User user, UserGroup ug "
        + "    WHERE "
        + "        user.group.id = ug.id "
        + "        AND user.deletedAt IS NULL "
        + "    GROUP BY "
        + "        ug.groupName) AS user_count_table ON userGroup.groupName = user_count_table.group_name "
        + "LEFT JOIN "
        + "    (SELECT "
        + "        ug.groupName AS group_name, "
        + "        COUNT(vote.id) AS vote_count "
        + "    FROM "
        + "        User user "
        + "    LEFT JOIN "
        + "        Vote vote ON user.id = vote.sender.id "
        + "    INNER JOIN "
        + "        UserGroup ug ON user.group.id = ug.id"
        + "    WHERE vote.createdAt >= ?1 "
        + "    GROUP BY "
        + "        ug.groupName) AS vote_count_table ON userGroup.groupName = vote_count_table.group_name "
        + "GROUP BY userGroup.groupName, user_count_table.group_name, vote_count_table.group_name "
        + "ORDER BY "
        + "    user_count_table.user_count DESC, vote_count_table.vote_count DESC")
    List<NewStatisticsUserGroupVO> getNewUserGroupStatistics(LocalDateTime voteStartAt);

    @Query("select count(sug) from StatisticsUserGroup sug")
    Long countSchoolAttackStatistics();

    @Query("select count(sug) from StatisticsUserGroup sug where sug.groupName like CONCAT('%',?1,'%' )")
    Long countSchoolAttackStatisticsContaining(String groupName);

    @Query("select sug.updatedAt from StatisticsUserGroup sug ORDER BY sug.updatedAt DESC LIMIT 1")
    LocalDateTime getSchoolAttackLastUpdatedAt();

    @Query("SELECT sug FROM StatisticsUserGroup sug ORDER BY sug.rankNumber")
    List<StatisticsUserGroup> getSchoolAttackStatistics(Pageable pageable);

    @Query("SELECT sug FROM StatisticsUserGroup sug where sug.groupName like CONCAT('%',?1,'%' ) ORDER BY sug.rankNumber")
    List<StatisticsUserGroup> getSchoolAttackStatisticsContaining(String groupName, Pageable pageable);
}
