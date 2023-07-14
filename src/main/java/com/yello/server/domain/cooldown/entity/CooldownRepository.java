package com.yello.server.domain.cooldown.entity;

import com.yello.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CooldownRepository extends JpaRepository<Cooldown, Long> {
    Optional<Cooldown> findByUser(User user);
}
