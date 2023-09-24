package com.yello.server.domain.user.repository;

import static com.yello.server.global.common.ErrorCode.AUTH_UUID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.DEVICE_TOKEN_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_NOT_FOUND_USER_EXCEPTION;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
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
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByIdNotFiltered(Long id) {
        return userJpaRepository.findByIdNotFiltered(id);
    }

    @Override
    public User getById(Long id) {
        return userJpaRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(USERID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public User getByIdNotFiltered(Long id) {
        return userJpaRepository.findByIdNotFiltered(id)
            .orElseThrow(() -> new UserNotFoundException(USERID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public Optional<User> findByUuid(String uuid) {
        return userJpaRepository.findByUuid(uuid);
    }

    @Override
    public Optional<User> findByUuidNotFiltered(String uuid) {
        return userJpaRepository.findByUuidNotFiltered(uuid);
    }

    @Override
    public User getByUuid(String uuid) {
        return userJpaRepository.findByUuid(uuid)
            .orElseThrow(() -> new UserNotFoundException(AUTH_UUID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public boolean existsByUuid(String uuid) {
        return userJpaRepository.existsByUuid(uuid);
    }

    @Override
    public User getByYelloId(String yelloId) {
        return userJpaRepository.findByYelloId(yelloId)
            .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public User getByYelloIdNotFiltered(String yelloId) {
        return userJpaRepository.findByYelloIdNotFiltered(yelloId)
            .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public Optional<User> findByYelloId(String yelloId) {
        return userJpaRepository.findByYelloId(yelloId);
    }

    @Override
    public Optional<User> findByYelloIdNotFiltered(String yelloId) {
        return userJpaRepository.findByYelloIdNotFiltered(yelloId);
    }

    @Override
    public User getByDeviceToken(String deviceToken) {
        return userJpaRepository.findByDeviceToken(deviceToken)
            .orElseThrow(() -> new UserNotFoundException(DEVICE_TOKEN_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public User getByDeviceTokenNotFiltered(String deviceToken) {
        return userJpaRepository.findByDeviceTokenNotFiltered(deviceToken)
            .orElseThrow(() -> new UserNotFoundException(DEVICE_TOKEN_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public Optional<User> findByDeviceToken(String deviceToken) {
        return userJpaRepository.findByDeviceToken(deviceToken);
    }

    @Override
    public Optional<User> findByDeviceTokenNotFiltered(String deviceToken) {
        return userJpaRepository.findByDeviceTokenNotFiltered(deviceToken);
    }

    @Override
    public List<User> findAllByGroupId(Long groupId) {
        return userJpaRepository.findAllByGroupId(groupId);
    }

    @Override
    public List<User> findAllByGroupContainingName(String groupName, String keyword,
        List<String> uuidList) {
        return userJpaRepository.findAllByGroupContainingName(groupName, keyword, uuidList);
    }

    @Override
    public List<User> findAllByOtherGroupContainingName(String groupName, String keyword,
        List<String> uuidList) {
        return userJpaRepository.findAllByOtherGroupContainingName(groupName, keyword, uuidList);
    }

    @Override
    public List<User> findAllByGroupContainingYelloId(String groupName, String keyword,
        List<String> uuidList) {
        return userJpaRepository.findAllByGroupContainingYelloId(groupName, keyword, uuidList);
    }

    @Override
    public List<User> findAllByOtherGroupContainingYelloId(String groupName, String keyword,
        List<String> uuidList) {
        return userJpaRepository.findAllByOtherGroupContainingYelloId(groupName, keyword, uuidList);
    }

    @Override
    public Long count() {
        return userJpaRepository.count();
    }

    @Override
    public Long countAllByYelloIdContaining(String yelloId) {
        return userJpaRepository.countAllByYelloIdContaining(yelloId);
    }

    @Override
    public Long countAllByNameContaining(String name) {
        return userJpaRepository.countAllByNameContaining(name);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userJpaRepository.findAll(pageable);
    }

    @Override
    public Page<User> findAllByYelloIdContaining(Pageable pageable, String yelloId) {
        return userJpaRepository.findAllByYelloIdContaining(pageable, yelloId);
    }

    @Override
    public Page<User> findAllByNameContaining(Pageable pageable, String name) {
        return userJpaRepository.findAllByNameContaining(pageable, name);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(user);
    }
}
