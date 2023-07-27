package com.yello.server.domain.friend.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

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
    Optional<Friend> findByUserAndTarget(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("select f from Friend f "
            + "where f.user.id = :userId "
            + "and f.target.deletedAt is null "
            + "order by f.target.name asc")
    Page<Friend> findAllFriendsByUserId(Pageable pageable, @Param("userId") Long userId);

    @Query("select f from Friend f " +
            "where f.user.id = :userId " +
            "and f.user.deletedAt is null " +
            "and f.target.deletedAt is null")
    List<Friend> findAllByUserId(Long userId);

    @Query("select f from Friend f " +
            "where f.target.id = :targetId " +
            "and f.user.deletedAt is null " +
            "and f.target.deletedAt is null")
    List<Friend> findAllByTargetId(Long targetId);

    @Query("select f from Friend f " +
            "where f.user.id = :userId")
    List<Friend> findAllByUserIdNotFiltered(Long userId);

    @Query("select f from Friend f " +
            "where f.target.id = :targetId")
    List<Friend> findAllByTargetIdNotFiltered(Long targetId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete from Friend f " +
            "where f.target.id = :targetId " +
            "and f.user.id = :userId")
    void deleteByUserAndTarget(@Param("userId") Long userId, @Param("targetId") Long targetId);
}
