package com.yello.server.domain.vote.repository;

import com.yello.server.domain.vote.entity.Vote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface VoteRepository {

    Vote save(Vote vote);

    Vote getById(Long id);

    Optional<Vote> findById(Long id);

    Integer countAllByReceiverUserId(Long userId);

    List<Vote> findAllByReceiverUserId(Long userId, Pageable pageable);

    List<Vote> findAllReceivedByFriends(Long userId, Pageable pageable);
}
