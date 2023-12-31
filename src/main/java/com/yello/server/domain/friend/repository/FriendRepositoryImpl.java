package com.yello.server.domain.friend.repository;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_FRIEND_EXCEPTION;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.exception.FriendNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendRepositoryImpl implements FriendRepository {

    private final FriendJpaRepository friendJpaRepository;

    @Override
    public Friend save(Friend friend) {
        return friendJpaRepository.save(friend);
    }

    @Override
    public void delete(Friend friend) {
        friendJpaRepository.delete(friend);
    }

    @Override
    public Integer countAllByUserId(Long userId) {
        return friendJpaRepository.countAllByUserId(userId);
    }

    @Override
    public Optional<Friend> findByUserAndTarget(Long userId, Long targetId) {
        return friendJpaRepository.findByUserAndTarget(userId, targetId);
    }

    @Override
    public Optional<Friend> findByUserAndTargetNotFiltered(Long userId, Long targetId) {
        return friendJpaRepository.findByUserAndTargetNotFiltered(userId, targetId);
    }

    @Override
    public Friend getByUserAndTarget(Long userId, Long targetId) {
        return friendJpaRepository.findByUserAndTarget(userId, targetId)
            .orElseThrow(() -> new FriendNotFoundException(NOT_FOUND_FRIEND_EXCEPTION));
    }

    @Override
    public boolean existsByUserAndTarget(Long userId, Long targetId) {
        return friendJpaRepository.existsUserAndTarget(userId, targetId);
    }

    @Override
    public Page<Friend> findAllFriendsByUserId(Pageable pageable, Long userId) {
        return friendJpaRepository.findAllFriendsByUserId(pageable, userId);
    }

    @Override
    public List<Friend> findAllByUserId(Long userId) {
        return friendJpaRepository.findAllByUserId(userId);
    }

    @Override
    public List<Friend> findAllByTargetId(Long targetId) {
        return friendJpaRepository.findAllByTargetId(targetId);
    }

    @Override
    public List<Friend> findAllByUserIdNotFiltered(Long userId) {
        return friendJpaRepository.findAllByUserIdNotFiltered(userId);
    }

    @Override
    public List<Friend> findAllByTargetIdNotFiltered(Long targetId) {
        return friendJpaRepository.findAllByTargetIdNotFiltered(targetId);
    }

    @Override
    public List<Friend> findAllByUserIdNotIn(Long userId, List<String> uuidList) {
        return friendJpaRepository.findALlByUserIdAndTargetNotIn(userId, uuidList);
    }
}
