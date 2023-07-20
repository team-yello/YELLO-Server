package com.yello.server.domain.user.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByName(String name);

    @Query(value = "select * from user where uuid = :uuid", nativeQuery = true)
    Optional<User> findByUuid(String uuid);

    Optional<User> findByYelloId(String yelloId);

    Slice<User> findAllByGroupId(Long groupId, Pageable pageable);

    List<User> findAllByGroupId(Long groupId);

    Slice<User> findAllByUuidIn(List<String> uuidList, Pageable pageable);
}
