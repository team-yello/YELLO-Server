package com.yello.server.domain.vote.entity;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("select count(v) from Vote v where v.receiver.id = :userId and v.receiver.deletedAt is null and v.sender.deletedAt is null")
    Integer countAllByReceiverUserId(@Param("userId") Long userId);

    @Query("select v from Vote v "
        + "where v.receiver.id = :userId "
        + "and v.receiver.deletedAt is null "
        + "and v.sender.deletedAt is null "
        + "order by v.createdAt desc")
    List<Vote> findAllByReceiverUserId(@Param("userId") Long userId, Pageable pageable);
}
 