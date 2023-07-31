package com.yello.server.domain.cooldown.entity;

import java.util.Optional;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CooldownRepository extends JpaRepository<Cooldown, Long> {

    @Query("select c from Cooldown c where c.user.id = :userId and c.user.deletedAt is null")
    Optional<Cooldown> findByUserId(@Param("userId") Long userId);

    @Query("select c from Cooldown c where c.user.id = :userId")
    Optional<Cooldown> findByUserIdNotFiltered(Long userId);

}
