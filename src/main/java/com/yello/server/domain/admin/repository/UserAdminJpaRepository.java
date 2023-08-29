package com.yello.server.domain.admin.repository;

import com.yello.server.domain.admin.entity.UserAdmin;
import com.yello.server.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdminJpaRepository extends JpaRepository<UserAdmin, Long> {

    Optional<UserAdmin> findByUser(User user);
}
