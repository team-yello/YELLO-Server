package com.yello.server.domain.admin.repository;

import com.yello.server.domain.admin.entity.UserAdmin;
import com.yello.server.domain.user.entity.User;
import java.util.Optional;

public interface UserAdminRepository {

    UserAdmin getByUser(User user);

    Optional<UserAdmin> findByUser(User user);
}
