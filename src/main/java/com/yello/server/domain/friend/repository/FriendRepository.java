package com.yello.server.domain.friend.repository;

import com.yello.server.domain.friend.entity.Friend;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendRepository {

    Friend save(Friend friend);

    void delete(Friend friend);

    Integer countAllByUserId(Long userId);

    Optional<Friend> findByUserAndTarget(Long userId, Long targetId);

    Optional<Friend> findByUserAndTargetNotFiltered(Long userId, Long targetId);

    Friend getByUserAndTarget(Long userId, Long targetId);

    boolean existsByUserAndTarget(Long userId, Long targetId);

    Page<Friend> findAllFriendsByUserId(Pageable pageable, Long userId);

    List<Friend> findAllByUserId(Long userId);

    List<Friend> findAllByTargetId(Long targetId);

    List<Friend> findAllByUserIdNotFiltered(Long userId);

    List<Friend> findAllByTargetIdNotFiltered(Long targetId);

    List<Friend> findAllByUserIdNotIn(Long userId, List<String> uuidList);
}
