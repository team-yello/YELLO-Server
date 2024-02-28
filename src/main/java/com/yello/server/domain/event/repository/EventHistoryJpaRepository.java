package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventHistory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventHistoryJpaRepository extends JpaRepository<EventHistory, Long> {

    @Query("select e from EventHistory e where e.idempotencyKey = ?1")
    Optional<EventHistory> findTopByIdempotencyKey(UUID idempotencyKey);

}
