package com.yello.server.domain.vote.repository;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_VOTE_EXCEPTION;

import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteRepositoryImpl implements VoteRepository {

    private final VoteJpaRepository voteJpaRepository;

    @Transactional
    @Override
    public Vote save(Vote vote) {
        return voteJpaRepository.save(vote);
    }

    @Override
    public Vote getById(Long id) {
        return voteJpaRepository.findById(id)
            .orElseThrow(() -> new VoteNotFoundException(NOT_FOUND_VOTE_EXCEPTION));
    }

    @Override
    public Optional<Vote> findById(Long id) {
        return voteJpaRepository.findById(id);
    }

    @Override
    public Integer countAllByReceiverUserId(Long userId) {
        return voteJpaRepository.countAllByReceiverUserId(userId);
    }

    @Override
    public Integer countUnreadByReceiverUserId(Long userId) {
        return voteJpaRepository.countUnreadByReceiverUserId(userId);
    }

    @Override
    public List<Vote> findAllByReceiverUserId(Long userId, Pageable pageable) {
        return voteJpaRepository.findAllByReceiverUserId(userId, pageable);
    }

    @Override
    public List<Vote> findAllReceivedByFriends(Long userId, Pageable pageable) {
        return voteJpaRepository.findAllReceivedByFriends(userId, pageable);
    }

    @Override
    public Integer countAllReceivedByFriends(Long userId) {
        return voteJpaRepository.countAllReceivedByFriends(userId);
    }
}
