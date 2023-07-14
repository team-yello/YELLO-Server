package com.yello.server.domain.user.entity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Create
    Optional<User> findById(Long id);

    Optional<User> findByName(String name);

    Optional<User> findByUuid(String uuid);

    Optional<User> findByYelloId(String yelloId);

    Slice<User> findAllByGroupId(Long groupId, Pageable pageable);

    Slice<User> findAllByUuidIn(List<String> uuidList, Pageable pageable);

    // Read

    // Update

    // Delete
}
