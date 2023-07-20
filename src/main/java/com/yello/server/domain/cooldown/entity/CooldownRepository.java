package com.yello.server.domain.cooldown.entity;

import com.yello.server.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CooldownRepository extends JpaRepository<Cooldown, Long> {

    @Query("select c from Cooldown c where c.user.id = :userId and c.user.deletedAt is null")
    Optional<Cooldown> findByUser(User user);

    @Query("select c from Cooldown c where c.user.id = :userId")
    Optional<Cooldown> findByUserIdNotFiltered(Long userId);
}
