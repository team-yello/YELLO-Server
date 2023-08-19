package com.yello.server.domain.friend.repository;

import com.yello.server.domain.friend.entity.Friend;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendJpaRepository extends JpaRepository<Friend, Long> {

    @Query("select count(f) from Friend f " +
        "where f.user.id = :userId " +
        "and f.user.deletedAt is null " +
        "and f.target.deletedAt is null")
    Integer countAllByUserId(@Param("userId") Long userId);

    @Query("select f from Friend f " +
        "where f.target.id = :targetId " +
        "and f.user.id = :userId " +
        "and f.user.deletedAt is null " +
        "and f.target.deletedAt is null " +
        "and f.deletedAt is null")
    Optional<Friend> findByUserAndTarget(@Param("userId") Long userId,
        @Param("targetId") Long targetId);

    @Query("select f from Friend f " +
        "where f.target.id = :targetId " +
        "and f.user.id = :userId")
    Optional<Friend> findByUserAndTargetNotFiltered(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("select case when count(f) > 0 then true else false end from Friend f " +
        "where f.target.id = :targetId " +
        "and f.user.id = :userId " +
        "and f.user.deletedAt is null " +
        "and f.target.deletedAt is null " +
        "and f.deletedAt is null")
    boolean existsUserAndTarget(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("select f from Friend f "
        + "where f.user.id = :userId "
        + "and f.target.deletedAt is null "
        + "order by f.target.name asc")
    Page<Friend> findAllFriendsByUserId(Pageable pageable, @Param("userId") Long userId);

    @Query("select f from Friend f " +
        "where f.user.id = :userId " +
        "and f.user.deletedAt is null " +
        "and f.target.deletedAt is null")
    List<Friend> findAllByUserId(@Param("userId") Long userId);

    @Query("select f from Friend f " +
        "where f.target.id = :targetId " +
        "and f.user.deletedAt is null " +
        "and f.target.deletedAt is null")
    List<Friend> findAllByTargetId(@Param("targetId") Long targetId);

    @Query("select f from Friend f " +
        "where f.user.id = :userId")
    List<Friend> findAllByUserIdNotFiltered(@Param("userId") Long userId);

    @Query("select f from Friend f " +
        "where f.target.id = :targetId")
    List<Friend> findAllByTargetIdNotFiltered(@Param("targetId") Long targetId);
}
