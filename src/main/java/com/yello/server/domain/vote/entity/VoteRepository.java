package com.yello.server.domain.vote.entity;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("select count(v) from Vote v where v.receiver.id = :userId")
    Integer getCountAllByReceiverUserId(@Param("userId") Long userId);
    
    @Query("select v from Vote v where v.receiver.id = :userId")
    List<Vote> findAllByReceiverUserId(@Param("userId") Long userId, Pageable pageable);


}
