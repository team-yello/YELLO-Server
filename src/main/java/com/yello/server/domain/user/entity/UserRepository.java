package com.yello.server.domain.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // CREATE
    User save(User user);

    // READ
    Optional<User> findById(Long id);
    Optional<User> findByUuid(String uuid);
    Optional<User> findAllByName(String name);

    // UPDATE

    // DELETE
  
}