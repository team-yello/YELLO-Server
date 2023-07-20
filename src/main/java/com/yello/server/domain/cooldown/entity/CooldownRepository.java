package com.yello.server.domain.cooldown.entity;

import com.yello.server.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CooldownRepository extends JpaRepository<Cooldown, Long> {

    Optional<Cooldown> findByUser(User user);
}
