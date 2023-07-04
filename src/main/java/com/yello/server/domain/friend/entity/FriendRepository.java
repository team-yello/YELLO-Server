package com.yello.server.domain.friend.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("select f from Friend f where f.follower.id = :friendId and f.following.id = :userId")
    Friend findByFollowingAndFollower(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
