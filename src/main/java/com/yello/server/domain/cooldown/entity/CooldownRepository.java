package com.yello.server.domain.cooldown.entity;

import com.yello.server.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CooldownRepository extends JpaRepository<Cooldown, Long> {

    Optional<Cooldown> findByUser(User user);

    @Query(value = "select * from cooldown where user_id = :userId", nativeQuery = true)
    Optional<Cooldown> findByUserIdNotFiltered(Long userId);
}
