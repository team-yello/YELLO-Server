package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventRandom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRandomJpaRepository extends JpaRepository<EventRandom, Long> {

    Optional<EventRandom> findTopByRandomTag(String randomTag);
}
