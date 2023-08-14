package com.yello.server.domain.user.repository;

import static com.yello.server.global.common.ErrorCode.AUTH_UUID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_NOT_FOUND_USER_EXCEPTION;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Transactional
    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public User getById(Long id) {
        return userJpaRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(USERID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public Optional<User> findByUuid(String uuid) {
        return userJpaRepository.findByUuid(uuid);
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
    public Optional<User> findByYelloId(String yelloId) {
        return userJpaRepository.findByYelloId(yelloId);
    }

    @Override
    public User getByYelloId(String yelloId) {
        return userJpaRepository.findByYelloId(yelloId)
            .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public Optional<User> findByDeviceToken(String deviceToken) {
        return userJpaRepository.findByDeviceToken(deviceToken);
    }

    @Override
    public List<User> findAllByGroupId(Long groupId) {
        return userJpaRepository.findAllByGroupId(groupId);
    }

    @Override
    public List<User> findAllByGroupContainingName(Long groupId, String keyword) {
        return userJpaRepository.findAllByGroupContainingName(groupId, keyword);
    }

    @Override
    public List<User> findAllByOtherGroupContainingName(Long groupId, String keyword) {
        return userJpaRepository.findAllByOtherGroupContainingName(groupId, keyword);
    }

    @Override
    public List<User> findAllByGroupContainingYelloId(Long groupId, String keyword) {
        return userJpaRepository.findAllByGroupContainingYelloId(groupId, keyword);
    }

    @Override
    public List<User> findAllByOtherGroupContainingYelloId(Long groupId, String keyword) {
        return userJpaRepository.findAllByOtherGroupContainingYelloId(groupId, keyword);
    }
}
