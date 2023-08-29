package com.yello.server.domain.admin.repository;

import static com.yello.server.global.common.ErrorCode.USER_ADMIN_NOT_FOUND_EXCEPTION;

import com.yello.server.domain.admin.entity.UserAdmin;
import com.yello.server.domain.admin.exception.UserAdminNotFoundException;
import com.yello.server.domain.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAdminRepositoryImpl implements UserAdminRepository {

    private final UserAdminJpaRepository userAdminJpaRepository;

    @Override
    public UserAdmin getByUser(User user) {
        return userAdminJpaRepository.findByUser(user)
            .orElseThrow(() -> new UserAdminNotFoundException(USER_ADMIN_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Optional<UserAdmin> findByUser(User user) {
        return userAdminJpaRepository.findByUser(user);
    }
}
