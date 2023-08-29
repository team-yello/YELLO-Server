package com.yello.server.domain.cooldown.repository;

import static com.yello.server.global.common.ErrorCode.ID_NOT_FOUND_COOLDOWN_EXCEPTION;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.exception.CooldownNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Cooldown getById(Long cooldownId) {
        return cooldownJpaRepository.findById(cooldownId)
            .orElseThrow(() -> new CooldownNotFoundException(ID_NOT_FOUND_COOLDOWN_EXCEPTION));
    }

    @Override
    public Optional<Cooldown> findById(Long cooldownId) {
        return cooldownJpaRepository.findById(cooldownId);
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
    public boolean existsByMessageId(String messageId) {
        return cooldownJpaRepository.existsByMessageId(messageId);
    }

    @Override
    public Optional<Cooldown> findByUserIdNotFiltered(Long userId) {
        return cooldownJpaRepository.findByUserIdNotFiltered(userId);
    }

    @Override
    public Long count() {
        return cooldownJpaRepository.count();
    }

    @Override
    public Long countAllByYelloIdContaining(String yelloId) {
        return cooldownJpaRepository.countAllByUserYelloIdContaining(yelloId);
    }

    @Override
    public Page<Cooldown> findAll(Pageable pageable) {
        return cooldownJpaRepository.findAll(pageable);
    }

    @Override
    public Page<Cooldown> findAllContaining(Pageable pageable, String yelloId) {
        return cooldownJpaRepository.findAllByUserYelloIdContaining(pageable, yelloId);
    }

    @Override
    public void delete(Cooldown cooldown) {
        cooldownJpaRepository.delete(cooldown);
    }
}
