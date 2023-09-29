package com.yello.server.domain.user;

import static com.yello.server.global.common.ErrorCode.AUTH_UUID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.DEVICE_TOKEN_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_NOT_FOUND_USER_EXCEPTION;

import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class FakeUserRepository implements UserRepository {

    private final List<User> data = new ArrayList<>();
    private Long id = 0L;

    private FriendRepository friendRepository;

    public FakeUserRepository(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @Override
    public User save(User user) {
        if (user.getId() != null && user.getId() > id) {
            id = user.getId();
        }

        User newUser = User.builder()
            .id(user.getId() == null ? ++id : user.getId())
            .recommendCount(0L)
            .name(user.getName())
            .yelloId(user.getYelloId())
            .gender(user.getGender())
            .point(2000)
            .social(user.getSocial())
            .profileImage(user.getProfileImage())
            .uuid(user.getUuid())
            .deletedAt(null)
            .group(user.getGroup())
            .groupAdmissionYear(user.getGroupAdmissionYear())
            .email(user.getEmail())
            .subscribe(user.getSubscribe()).ticketCount(user.getTicketCount())
            .deviceToken(user.getDeviceToken())
            .build();

        data.add(newUser);
        return newUser;
    }

    @Override
    public Optional<User> findById(Long id) {
        return data.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst();
    }

    @Override
    public Optional<User> findByIdNotFiltered(Long id) {
        return data.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst();
    }

    @Override
    public User getById(Long id) {
        return data.stream()
            .filter(user -> user.getDeletedAt() == null)
            .filter(user -> user.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(USERID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public User getByIdNotFiltered(Long id) {
        return data.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(USERID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public Optional<User> findByUuid(String uuid) {
        return data.stream()
            .filter(user -> user.getUuid().equals(uuid))
            .findFirst();
    }

    @Override
    public Optional<User> findByUuidNotFiltered(String uuid) {
        return data.stream()
            .filter(user -> user.getUuid().equals(uuid))
            .findFirst();
    }

    @Override
    public User getByUuid(String uuid) {
        return data.stream()
            .filter(user -> user.getUuid().equals(uuid))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(AUTH_UUID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public boolean existsByUuid(String uuid) {
        return data.stream()
            .anyMatch(user -> user.getUuid().equals(uuid));
    }

    @Override
    public Optional<User> findByYelloId(String yelloId) {
        return data.stream()
            .filter(user -> user.getDeletedAt() == null)
            .filter(user -> user.getYelloId().equals(yelloId))
            .findFirst();
    }

    @Override
    public Optional<User> findByYelloIdNotFiltered(String yelloId) {
        return data.stream()
            .filter(user -> user.getYelloId().equals(yelloId))
            .findFirst();
    }

    @Override
    public User getByDeviceToken(String deviceToken) {
        return data.stream()
            .filter(user -> user.getDeletedAt() == null)
            .filter(user -> user.getDeviceToken().equals(deviceToken))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(DEVICE_TOKEN_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public User getByDeviceTokenNotFiltered(String deviceToken) {
        return data.stream()
            .filter(user -> user.getDeviceToken().equals(deviceToken))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(DEVICE_TOKEN_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public User getByYelloId(String yelloId) {
        return data.stream()
            .filter(user -> user.getYelloId().equals(yelloId))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public User getByYelloIdNotFiltered(String yelloId) {
        return data.stream()
            .filter(user -> user.getYelloId().equals(yelloId))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public Optional<User> findByDeviceToken(String deviceToken) {
        return data.stream()
            .filter(user -> user.getDeviceToken().equals(deviceToken))
            .findFirst();
    }

    @Override
    public Optional<User> findByDeviceTokenNotFiltered(String deviceToken) {
        return data.stream()
            .filter(user -> user.getDeviceToken().equals(deviceToken))
            .findFirst();
    }

    @Override
    public List<User> findAllByGroupId(Long groupId) {
        return data.stream()
            .filter(user -> user.getGroup().getId().equals(groupId))
            .toList();
    }

    @Override
    public Integer countAllByGroupNameFilteredByNotFriend(Long userId, String groupName) {
        return data.stream()
            .filter(user -> !user.getId().equals(userId))
            .filter(user -> user.getGroup().getGroupName().equals(groupName))
            .filter(user ->
                !friendRepository.findAllByUserId(userId).stream()
                    .filter(friend -> friend.getTarget().getDeletedAt() == null)
                    .map(friend -> friend.getTarget().getId())
                    .toList()
                    .contains(user.getId())
            )
            .filter(user -> user.getDeletedAt() == null)
            .toList()
            .size();
    }

    @Override
    public List<User> findAllByGroupNameFilteredByNotFriend(Long userId, String groupName, Pageable pageable) {
        return data.stream()
            .filter(user -> !user.getId().equals(userId))
            .filter(user -> user.getGroup().getGroupName().equals(groupName))
            .filter(user ->
                !friendRepository.findAllByUserId(userId).stream()
                    .filter(friend -> friend.getTarget().getDeletedAt() != null)
                    .map(friend -> friend.getTarget().getId())
                    .toList()
                    .contains(user.getId())
            )
            .filter(user -> user.getDeletedAt() != null)
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
    }

    @Override
    public List<User> findAllByGroupContainingName(String groupName, String keyword,
        List<String> uuidList) {
        return data.stream()
            .filter(user -> user.getGroup().getGroupName().equals(groupName))
            .filter(user -> user.getName().contains(keyword))
            .filter(user -> !user.getId().equals(1L))
            .toList();
    }

    @Override
    public List<User> findAllByOtherGroupContainingName(String groupName, String keyword,
        List<String> uuidList) {
        return data.stream()
            .filter(user -> !user.getGroup().getGroupName().equals(groupName))
            .filter(user -> user.getName().contains(keyword))
            .filter(user -> !user.getId().equals(1L))
            .toList();
    }

    @Override
    public List<User> findAllByGroupContainingYelloId(String groupName, String keyword,
        List<String> uuidList) {
        return data.stream()
            .filter(user -> user.getGroup().getGroupName().equals(groupName))
            .filter(user -> user.getYelloId().contains(keyword))
            .filter(user -> !user.getId().equals(1L))
            .toList();
    }

    @Override
    public List<User> findAllByOtherGroupContainingYelloId(String groupName, String keyword,
        List<String> uuidList) {
        return data.stream()
            .filter(user -> !user.getGroup().getGroupName().equals(groupName))
            .filter(user -> user.getYelloId().contains(keyword))
            .filter(user -> !user.getId().equals(1L))
            .toList();
    }

    @Override
    public Long count() {
        return (long) data.size();
    }

    @Override
    public Long countAllByYelloIdContaining(String yelloId) {
        return (long) data.stream()
            .filter(user -> user.getYelloId().contains(yelloId))
            .toList()
            .size();
    }

    @Override
    public Long countAllByNameContaining(String name) {
        return (long) data.stream()
            .filter(user -> user.getName().contains(name))
            .toList()
            .size();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        final List<User> userList = data.stream()
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
        return new PageImpl<>(userList);
    }

    @Override
    public Page<User> findAllByYelloIdContaining(Pageable pageable, String yelloId) {
        final List<User> userList = data.stream()
            .filter(user -> user.getYelloId().contains(yelloId))
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
        return new PageImpl<>(userList);
    }

    @Override
    public Page<User> findAllByNameContaining(Pageable pageable, String name) {
        final List<User> userList = data.stream()
            .filter(user -> user.getName().contains(name))
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
        return new PageImpl<>(userList);
    }

    @Override
    public void delete(User user) {
        data.remove(user);
    }
}
