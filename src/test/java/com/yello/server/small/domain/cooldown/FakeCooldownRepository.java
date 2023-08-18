package com.yello.server.small.domain.cooldown;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeCooldownRepository implements CooldownRepository {

    private final List<Cooldown> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Cooldown save(Cooldown cooldown) {
        if (cooldown.getId()!=null && cooldown.getId() > id) {
            id = cooldown.getId();
        }

        Cooldown newCooldown = Cooldown.builder()
            .id(cooldown.getId()==null ? ++id : cooldown.getId())
            .user(cooldown.getUser())
            .createdAt(cooldown.getCreatedAt())
            .deletedAt(null)
            .build();

        data.add(newCooldown);
        return newCooldown;
    }

    @Override
    public Optional<Cooldown> findByUserId(Long userId) {
        return data.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst();
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
        data.remove(cooldown);
    }
}
