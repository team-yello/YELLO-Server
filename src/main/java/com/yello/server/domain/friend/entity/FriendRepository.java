package com.yello.server.domain.friend.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("select f from Friend f where f.target.id = :targetId and f.user.id = :userId")
    Friend findByFollowingAndFollower(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("select f from Friend f where f.user.id = :userId")
    Page<Friend> findAllFriendsByUserId(Pageable pageable, @Param("userId") Long userId);
}
