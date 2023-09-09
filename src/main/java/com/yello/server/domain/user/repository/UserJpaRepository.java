package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    @Query("select u from User u " +
        "where u.id = :id " +
        "and u.deletedAt is null")
    Optional<User> findById(@Param("id") Long id);

    @Query("select u from User u " +
        "where u.uuid = :uuid")
    Optional<User> findByUuid(@Param("uuid") String uuid);

    @Query("select u from User u " +
        "where u.uuid = :uuid")
    Optional<User> findByUuidNotFiltered(@Param("uuid") String uuid);

    @Query("select case when count(u) > 0 then true else false end from User u " +
        "where u.uuid = :uuid " +
        "and u.deletedAt is null")
    boolean existsByUuid(@Param("uuid") String uuid);

    @Query("select u from User u " +
        "where u.yelloId = :yelloId " +
        "and u.deletedAt is null")
    Optional<User> findByYelloId(@Param("yelloId") String yelloId);

    @Query("select u from User u " +
        "where u.yelloId = :yelloId")
    Optional<User> findByYelloIdNotFiltered(@Param("yelloId") String yelloId);

    @Query("select u from User u " +
        "where u.group.schoolName = :schoolName " +
        "and u.deletedAt is null")
    List<User> findAllByGroupId(@Param("schoolName") String schoolName);


    @Query("select u from User u "
        + "where u.group.schoolName = :groupName "
        + "and u.uuid not in :uuidList "
        + "and u.name like CONCAT('%', :keyword, '%') "
        + "and u.deletedAt is null "
        + "order by u.name ASC ")
    List<User> findAllByGroupContainingName(@Param("groupName") String groupName,
        @Param("keyword") String keyword, @Param("uuidList") List<String> uuidList);

    @Query("select u from User u "
        + "where u.group.schoolName <> :groupName "
        + "and u.uuid not in :uuidList "
        + "and u.name like CONCAT('%', :keyword, '%') "
        + "and u.deletedAt is null "
        + "order by u.groupAdmissionYear DESC ")
    List<User> findAllByOtherGroupContainingName(@Param("groupName") String groupName,
        @Param("keyword") String keyword, @Param("uuidList") List<String> uuidList);

    @Query("select u from User u "
        + "where u.group.schoolName = :groupName "
        + "and u.uuid not in :uuidList "
        + "and LOWER(u.yelloId) like LOWER(CONCAT('%', :keyword, '%')) "
        + "and u.deletedAt is null "
        + "order by u.yelloId ASC ")
    List<User> findAllByGroupContainingYelloId(@Param("groupName") String groupName,
        @Param("keyword") String keyword, @Param("uuidList") List<String> uuidList);

    @Query("select u from User u "
        + "where u.group.schoolName <> :groupName "
        + "and u.uuid not in :uuidList "
        + "and LOWER(u.yelloId) like LOWER(CONCAT('%', :keyword, '%')) "
        + "and u.deletedAt is null "
        + "order by u.groupAdmissionYear DESC ")
    List<User> findAllByOtherGroupContainingYelloId(@Param("groupName") String groupName,
        @Param("keyword") String keyword, @Param("uuidList") List<String> uuidList);

    @Query("select u from User u "
        + "where u.deviceToken = :deviceToken "
        + "and u.deletedAt is null")
    Optional<User> findByDeviceToken(@Param("deviceToken") String deviceToken);

    @Query("select u from User u " +
        "where u.deviceToken = :deviceToken")
    Optional<User> findByDeviceTokenNotFiltered(@Param("deviceToken") String deviceToken);

    Long countAllByYelloIdContaining(String yelloId);

    Long countAllByNameContaining(String name);

    @Query("select u from User u "
        + "where LOWER(u.yelloId) like LOWER(CONCAT('%', :yelloId, '%'))")
    Page<User> findAllByYelloIdContaining(Pageable pageable, @Param("yelloId") String yelloId);

    @Query("select u from User u "
        + "where LOWER(u.name) like LOWER(CONCAT('%', :name, '%'))")
    Page<User> findAllByNameContaining(Pageable pageable, @Param("name") String name);
}
