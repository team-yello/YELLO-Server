package com.yello.server.domain.friend.repository;

import com.yello.server.domain.friend.entity.Friend;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendRepository {

    Friend save(Friend friend);

    Integer countAllByUserId(Long userId);

    Friend findByUserAndTarget(Long userId, Long targetId);

    boolean existsByUserAndTarget(Long userId, Long targetId);

    Page<Friend> findAllFriendsByUserId(Pageable pageable, Long userId);

    List<Friend> findAllByUserId(Long userId);

    List<Friend> findAllByTargetId(Long targetId);

    List<Friend> findAllByUserIdNotFiltered(Long userId);

    List<Friend> findAllByTargetIdNotFiltered(Long targetId);

    void deleteByUserAndTarget(Long userId, Long targetId);
}
