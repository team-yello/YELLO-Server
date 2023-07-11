package com.yello.server.domain.user.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByName(String name);

    Optional<User> findByUuid(String uuid);
}
