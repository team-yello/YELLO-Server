package com.yello.server.domain.group.repository;

import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserGroupJpaRepository extends JpaRepository<UserGroup, Long> {

    @Query("select count (distinct(s.groupName)) from UserGroup s " +
        "where s.groupName " +
        "like CONCAT('%',:groupName,'%' )" +
        "and s.userGroupType = :userGroupType")
    Integer countDistinctGroupNameContaining(@Param("groupName") String groupName,
        @Param("userGroupType") UserGroupType userGroupType);

    @Query("select distinct s.groupName from UserGroup s " +
        "where s.groupName " +
        "like CONCAT('%',:groupName,'%' )" +
        "and s.userGroupType = :userGroupType")
    List<String> findDistinctGroupNameContaining(@Param("groupName") String groupName,
        @Param("userGroupType") UserGroupType userGroupType,
        Pageable pageable);

    @Query("select count (s) from UserGroup s " +
        "where s.groupName " +
        "like CONCAT('%',:groupName,'%') " +
        "and s.userGroupType = :userGroupType")
    Integer countAllContaining(@Param("groupName") String groupName,
        @Param("userGroupType") UserGroupType userGroupType);

    @Query("select s from UserGroup s " +
        "where s.groupName " +
        "like CONCAT('%',:groupName,'%' )" +
        "and s.userGroupType = :userGroupType")
    List<UserGroup> findAllContaining(@Param("groupName") String groupName,
        @Param("userGroupType") UserGroupType userGroupType,
        Pageable pageable);

    @Query("select count (s) from UserGroup s " +
        "where s.groupName = :groupName " +
        "and s.subGroupName like CONCAT('%',:subGroupName,'%') " +
        "and s.userGroupType = :userGroupType")
    Integer countAllByGroupNameContaining(@Param("groupName") String groupName,
        @Param("subGroupName") String subGroupName,
        @Param("userGroupType") UserGroupType userGroupType);

    @Query("select s from UserGroup s " +
        "where s.groupName = :groupName " +
        "and s.subGroupName like CONCAT('%',:subGroupName,'%') " +
        "and s.userGroupType = :userGroupType")
    List<UserGroup> findAllByGroupNameContaining(@Param("groupName") String groupName,
        @Param("subGroupName") String subGroupName,
        @Param("userGroupType") UserGroupType userGroupType,
        Pageable pageable);

    @Query("select s from UserGroup s " +
        "where s.groupName = :groupName " +
        "and s.subGroupName = :subGroupName " +
        "and s.userGroupType = :userGroupType")
    Optional<UserGroup> findByGroupNameAndSubGroupName(
        @Param("groupName") String groupName, @Param("subGroupName") String subGroupName,
        @Param("userGroupType") UserGroupType userGroupType);
}