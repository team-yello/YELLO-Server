package com.yello.server.domain.vote.entity;

import com.yello.server.domain.friend.entity.Friend;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("select v from Vote v where v.receiver.id = :userId")
    List<Friend> findAllByReceiverUserId(@Param("userId") Long userId);
}
