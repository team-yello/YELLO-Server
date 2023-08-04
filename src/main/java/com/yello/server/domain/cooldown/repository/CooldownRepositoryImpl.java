package com.yello.server.domain.cooldown.repository;

import com.yello.server.domain.cooldown.entity.Cooldown;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CooldownRepositoryImpl implements CooldownRepository {

  private final CooldownJpaRepository cooldownJpaRepository;

  @Override
  public Cooldown save(Cooldown cooldown) {
    return cooldownJpaRepository.save(cooldown);
  }

  @Override
  public Optional<Cooldown> findByUserId(Long userId) {
    return cooldownJpaRepository.findByUserId(userId);
  }

  @Override
  public boolean existsByUserId(Long userId) {
    return cooldownJpaRepository.existsByUserId(userId);
  }

  @Override
  public Optional<Cooldown> findByUserIdNotFiltered(Long userId) {
    return cooldownJpaRepository.findByUserIdNotFiltered(userId);
  }

  @Override
  public void delete(Cooldown cooldown) {
    cooldownJpaRepository.delete(cooldown);
  }
}
