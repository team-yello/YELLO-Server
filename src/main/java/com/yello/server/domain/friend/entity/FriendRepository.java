package com.yello.server.domain.friend.entity;

import com.yello.server.domain.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("select f from Friend f where f.target.id = :targetId and f.user.id = :userId")
    Friend findByFollowingAndFollower(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("select f from Friend f where f.user.id = :userId")
    Page<Friend> findAllFriendsByUserId(Pageable pageable, @Param("userId") Long userId);

    List<Friend> findAllByUser(User user);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete from Friend f where f.target.id = :targetId and f.user.id = :userId")
    void deleteByFollowingAndFollower(@Param("userId") Long userId, @Param("targetId") Long targetId);
}
