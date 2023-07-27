package com.yello.server.domain.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u " +
            "where u.id = :id " +
            "and u.deletedAt is null")
    Optional<User> findById(Long id);

    @Query("select u from User u " +
            "where u.uuid = :uuid")
    Optional<User> findByUuid(String uuid);

    @Query("select u from User u " +
            "where u.uuid = :uuid " +
            "and u.deletedAt is null")
    Optional<User> findExistsByUuid(String uuid);

    @Query("select u from User u " +
            "where u.yelloId = :yelloId " +
            "and u.deletedAt is null")
    Optional<User> findByYelloId(String yelloId);

    @Query("select u from User u " +
            "where u.group.id = :groupId " +
            "and u.deletedAt is null")
    List<User> findAllByGroupId(Long groupId);
}
