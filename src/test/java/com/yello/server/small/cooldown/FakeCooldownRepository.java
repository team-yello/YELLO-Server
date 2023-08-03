package com.yello.server.small.cooldown;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import java.util.Optional;

public class FakeCooldownRepository implements CooldownRepository {

    @Override
    public void save(Cooldown cooldown) {

    }

    @Override
    public Optional<Cooldown> findByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return false;
    }

    @Override
    public Optional<Cooldown> findByUserIdNotFiltered(Long userId) {
        return Optional.empty();
    }

    @Override
    public void delete(Cooldown cooldown) {

    }
}
