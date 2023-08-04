package com.yello.server.small.friend;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_FRIEND_EXCEPTION;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.exception.FriendNotFoundException;
import com.yello.server.domain.friend.repository.FriendRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class FakeFriendRepository implements FriendRepository {

    private List<Friend> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Friend save(Friend friend) {
        if (friend.getId() != null && friend.getId() > id) {
            id = friend.getId();
        }

        Friend newFriend = Friend.builder()
            .id(friend.getId() == null ? ++id : friend.getId())
            .user(friend.getUser())
            .target(friend.getTarget())
            .deletedAt(null)
            .build();

        data.add(newFriend);
        return newFriend;
    }

    @Override
    public Integer countAllByUserId(Long userId) {
        return data.stream()
            .filter(friend -> friend.getUser().getId().equals(userId) && friend.getDeletedAt() == null)
            .toList()
            .size();
    }

    @Override
    public Friend findByUserAndTarget(Long userId, Long targetId) {
        return data.stream()
            .filter(friend -> friend.getUser().getId().equals(userId) &&
                friend.getTarget().getId().equals(targetId)
                && friend.getDeletedAt() == null)
            .findFirst()
            .orElseThrow(() -> new FriendNotFoundException(NOT_FOUND_FRIEND_EXCEPTION));
    }

    @Override
    public boolean existsByUserAndTarget(Long userId, Long targetId) {
        return data.stream()
            .anyMatch(friend -> friend.getUser().getId().equals(userId) &&
                friend.getTarget().getId().equals(targetId)
                && friend.getDeletedAt() == null);
    }

    @Override
    public Page<Friend> findAllFriendsByUserId(Pageable pageable, Long userId) {
        final List<Friend> friends = data.stream()
            .filter(friend -> friend.getUser().getId().equals(userId) && friend.getDeletedAt() == null)
            .toList();

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), friends.size());
        return new PageImpl<>(friends.subList(start, end), pageable, friends.size());
    }

    @Override
    public List<Friend> findAllByUserId(Long userId) {
        return data.stream()
            .filter(friend -> friend.getUser().getId().equals(userId) && friend.getDeletedAt() == null)
            .toList();
    }

    @Override
    public List<Friend> findAllByTargetId(Long targetId) {
        return data.stream()
            .filter(
                friend -> friend.getTarget().getId().equals(targetId) && friend.getDeletedAt() == null)
            .toList();
    }

    @Override
    public List<Friend> findAllByUserIdNotFiltered(Long userId) {
        return data.stream()
            .filter(friend -> friend.getUser().getId().equals(userId))
            .toList();
    }

    @Override
    public List<Friend> findAllByTargetIdNotFiltered(Long targetId) {
        return data.stream()
            .filter(friend -> friend.getTarget().getId().equals(targetId))
            .toList();
    }
}
