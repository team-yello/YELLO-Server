package com.yello.server.domain.cooldown.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CooldownRepository extends JpaRepository<Cooldown, Long> {

}
