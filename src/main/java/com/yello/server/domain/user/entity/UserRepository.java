package com.yello.server.domain.user.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select u from User u where u.id = :id and u.deletedAt is null")
    Optional<User> findById(Long id);

    @Query(value = "select u from User u where u.uuid = :uuid")
    Optional<User> findByUuid(String uuid);

    @Query(value = "select u from User u where u.yelloId = :yelloId and u.deletedAt is null")
    Optional<User> findByYelloId(String yelloId);

    @Query(value = "select u from User u where u.group.id = :groupId and u.deletedAt is null")
    List<User> findAllByGroupId(Long groupId);
}
