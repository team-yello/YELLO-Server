package com.yello.server.domain.user.repository;

import static com.yello.server.global.common.ErrorCode.AUTH_UUID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_NOT_FOUND_USER_EXCEPTION;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import java.util.List;
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
    public User findById(Long id) {
        return userJpaRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(USERID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public User findByUuid(String uuid) {
        return userJpaRepository.findByUuid(uuid)
            .orElseThrow(() -> new UserNotFoundException(AUTH_UUID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public boolean existsByUuid(String uuid) {
        return userJpaRepository.existsByUuid(uuid);
    }

    @Override
    public User findByYelloId(String yelloId) {
        return userJpaRepository.findByYelloId(yelloId)
            .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public List<User> findAllByGroupId(Long groupId) {
        return userJpaRepository.findAllByGroupId(groupId);
    }
}
