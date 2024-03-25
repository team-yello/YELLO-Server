package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.User;
import java.time.LocalDateTime;
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
        "where u.id = :id")
    Optional<User> findByIdNotFiltered(@Param("id") Long id);

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

    @Query("select u from User u, UserGroup g " +
        "where u.group.id = g.id " +
        "and g.id = :groupId " +
        "and u.deletedAt is null")
    List<User> findAllByGroupId(@Param("groupId") Long groupId);

    @Query("select count (u) from User u, UserGroup g " +
        "where u.group.id = g.id " +
        "and g.groupName = :groupName " +
        "and u.id <> :userId " +
        "and u.id not in (select f.target.id from Friend f where :userId = f.user.id and f.target.deletedAt is null) "
        +
        "and u.deletedAt is null")
    Integer countAllByGroupNameFilteredByNotFriend(@Param("userId") Long userId, @Param("groupName") String groupName);

    @Query("select u from User u, UserGroup g " +
        "where u.group.id = g.id " +
        "and g.groupName = :groupName " +
        "and u.id <> :userId " +
        "and u.id not in (select f.target.id from Friend f where :userId = f.user.id and f.target.deletedAt is null) "
        +
        "and u.deletedAt is null")
    List<User> findAllByGroupNameFilteredByNotFriend(@Param("userId") Long userId,
        @Param("groupName") String groupName, Pageable pageable);

    @Query("select u from User u "
        + "where u.group.groupName = :groupName "
        + "and u.uuid not in :uuidList "
        + "and u.name like CONCAT('%', :keyword, '%') "
        + "and u.deletedAt is null "
        + "order by u.name ASC ")
    List<User> findAllByGroupContainingName(@Param("groupName") String groupName,
        @Param("keyword") String keyword, @Param("uuidList") List<String> uuidList);

    @Query("select u from User u "
        + "where u.group.groupName <> :groupName "
        + "and u.uuid not in :uuidList "
        + "and u.name like CONCAT('%', :keyword, '%') "
        + "and u.deletedAt is null "
        + "order by u.groupAdmissionYear DESC ")
    List<User> findAllByOtherGroupContainingName(@Param("groupName") String groupName,
        @Param("keyword") String keyword, @Param("uuidList") List<String> uuidList);

    @Query("select u from User u "
        + "where u.group.groupName = :groupName "
        + "and u.uuid not in :uuidList "
        + "and LOWER(u.yelloId) like LOWER(CONCAT('%', :keyword, '%')) "
        + "and u.deletedAt is null "
        + "order by u.yelloId ASC ")
    List<User> findAllByGroupContainingYelloId(@Param("groupName") String groupName,
        @Param("keyword") String keyword, @Param("uuidList") List<String> uuidList);

    @Query("select u from User u "
        + "where u.group.groupName <> :groupName "
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

    @Query("SELECT count(u) FROM User u WHERE ?1 <= u.createdAt AND u.createdAt < ?2")
    Long count(LocalDateTime startCreatedAt, LocalDateTime endCreatedAt);

    @Query("SELECT count(u) FROM User u WHERE ?1 <= u.deletedAt AND u.deletedAt < ?2")
    Long countDeletedAt(LocalDateTime startDeletedAt, LocalDateTime endDeletedAt);

    Long countAllByYelloIdContaining(String yelloId);

    Long countAllByNameContaining(String name);

    Long countAllByGender(Gender gender);

    @Query("SELECT count(u) FROM User u WHERE u.gender = ?1 AND ?2 <= u.createdAt AND u.createdAt < ?3")
    Long countAllByGender(Gender gender, LocalDateTime startCreatedAt, LocalDateTime endCreatedAt);

    @Query("select u from User u "
        + "where LOWER(u.yelloId) like LOWER(CONCAT('%', :yelloId, '%'))")
    Page<User> findAllByYelloIdContaining(Pageable pageable, @Param("yelloId") String yelloId);

    @Query("select u from User u "
        + "where LOWER(u.name) like LOWER(CONCAT('%', :name, '%'))")
    Page<User> findAllByNameContaining(Pageable pageable, @Param("name") String name);

}
