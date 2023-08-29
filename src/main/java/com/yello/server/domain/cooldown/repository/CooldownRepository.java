package com.yello.server.domain.cooldown.repository;

import com.yello.server.domain.cooldown.entity.Cooldown;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CooldownRepository {

    Cooldown save(Cooldown cooldown);

    Cooldown getById(Long cooldownId);

    Optional<Cooldown> findById(Long cooldownId);

    Optional<Cooldown> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByMessageId(String messageId);

    Optional<Cooldown> findByUserIdNotFiltered(Long userId);

    Long count();

    Long countAllByYelloIdContaining(String yelloId);

    Page<Cooldown> findAll(Pageable pageable);

    Page<Cooldown> findAllContaining(Pageable pageable, String yelloId);

    void delete(Cooldown cooldown);
}
