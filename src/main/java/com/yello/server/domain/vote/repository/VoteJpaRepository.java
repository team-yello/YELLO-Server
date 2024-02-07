package com.yello.server.domain.vote.repository;

import com.yello.server.domain.vote.entity.Vote;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteJpaRepository extends JpaRepository<Vote, Long> {

    @Query("select count(v) from Vote v where v.receiver.id = :userId and v.receiver.deletedAt is null and v.sender.deletedAt is null")
    Integer countAllByReceiverUserId(@Param("userId") Long userId);

    @Query("select count(v) from Vote v "
        + "where v.receiver.id = :userId "
        + "and v.receiver.deletedAt is null "
        + "and v.sender.deletedAt is null "
        + "and v.isRead = false")
    Integer countUnreadByReceiverUserId(@Param("userId") Long userId);

    @Query("select count(v) from Vote v "
        + "where v.receiver.deviceToken = :deviceToken "
        + "and v.receiver.deletedAt is null "
        + "and v.sender.deletedAt is null "
        + "and v.isRead = false")
    Integer countUnreadByReceiverDeviceToken(@Param("deviceToken") String deviceToken);

    @Query("select v from Vote v "
        + "where v.receiver.id = :userId "
        + "and v.receiver.deletedAt is null "
        + "and v.sender.deletedAt is null "
        + "order by v.createdAt desc")
    List<Vote> findAllByReceiverUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select v from Vote v where v.receiver.id in "
        + "(select f.target.id from Friend f where f.user.id = :userId and f.deletedAt is null) "
        + "and v.sender.deletedAt is null "
        + "and v.receiver.deletedAt is null "
        + "order by v.createdAt desc")
    List<Vote> findAllReceivedByFriends(@Param("userId") Long userId, Pageable pageable);

    @Query("select count(v) from Vote v where v.receiver.id in "
        + "(select f.target.id from Friend f where f.user.id = :userId and f.deletedAt is null) "
        + "and v.sender.deletedAt is null "
        + "and v.receiver.deletedAt is null "
        + "order by v.createdAt desc")
    Integer countAllReceivedByFriends(@Param("userId") Long userId);


    @Query("select count(v) from Vote v "
        + "where v.receiver.id = :userId "
        + "and v.isRead = true "
        + "and v.receiver.deletedAt is null "
        + "and v.sender.deletedAt is null")
    Integer countReadByReceiverUserId(@Param("userId") Long userId);

    @Query("select count(v) from Vote v "
        + "where v.receiver.id = :userId "
        + "and v.isRead = true "
        + "and v.isAnswerRevealed = true "
        + "and v.receiver.deletedAt is null "
        + "and v.sender.deletedAt is null")
    Integer countOpenKeywordByReceiverUserId(@Param("userId") Long userId);

    @Query("select count(v) from Vote v "
        + "where v.receiver.id = :userId "
        + "and v.isRead = true "
        + "and v.nameHint in (0,1) "
        + "and v.receiver.deletedAt is null "
        + "and v.sender.deletedAt is null")
    Integer countOpenNameByReceiverUserId(@Param("userId") Long userId);


    @Query("select count(v) from Vote v "
        + "where v.receiver.id = :userId "
        + "and v.isRead = true "
        + "and v.nameHint = -2 "
        + "and v.receiver.deletedAt is null "
        + "and v.sender.deletedAt is null")
    Integer countOpenFullNameByReceiverUserId(@Param("userId") Long userId);


}
 