package com.yello.server.domain.cooldown.repository;

import com.yello.server.domain.cooldown.entity.Cooldown;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CooldownJpaRepository extends JpaRepository<Cooldown, Long> {

    @Query("select c from Cooldown c where c.user.id = :userId and c.user.deletedAt is null")
    Optional<Cooldown> findByUserId(Long userId);

    @Query("select c from Cooldown c where c.user.id = :userId")
    Optional<Cooldown> findByUserIdNotFiltered(Long userId);

}
