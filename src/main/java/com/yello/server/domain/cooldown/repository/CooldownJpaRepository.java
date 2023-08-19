package com.yello.server.domain.cooldown.repository;

import com.yello.server.domain.cooldown.entity.Cooldown;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CooldownJpaRepository extends JpaRepository<Cooldown, Long> {

    @Query("select c from Cooldown c where c.user.id = :userId and c.user.deletedAt is null")
    Optional<Cooldown> findByUserId(@Param("userId") Long userId);

    @Query("select c from Cooldown c where c.user.id = :userId")
    Optional<Cooldown> findByUserIdNotFiltered(@Param("userId") Long userId);

    @Query("select case when count(c) > 0 then true else false end from Cooldown c " +
        "where c.user.id = :userId " +
        "and c.deletedAt is null")
    boolean existsByUserId(@Param("userId") Long userId);

    @Query("select case when count(c) > 0 then true else false end from Cooldown c " +
        "where c.messageId = :messageId " +
        "and c.deletedAt is null")
    boolean existsByMessageId(@Param("messageId") String messageId);
}
