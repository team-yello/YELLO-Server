package com.yello.server.entity.cooldown;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CooldownRepository extends JpaRepository<Cooldown, Long> {
}
