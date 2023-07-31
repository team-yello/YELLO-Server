package com.yello.server.domain.cooldown.repository;

import com.yello.server.domain.cooldown.entity.Cooldown;
import java.util.Optional;

public interface CooldownRepository {

    void save(Cooldown cooldown);

    Optional<Cooldown> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Optional<Cooldown> findByUserIdNotFiltered(Long userId);

    void delete(Cooldown cooldown);
}
