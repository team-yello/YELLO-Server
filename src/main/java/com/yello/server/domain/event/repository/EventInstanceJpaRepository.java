package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventHistory;
import com.yello.server.domain.event.entity.EventInstance;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventInstanceJpaRepository extends JpaRepository<EventInstance, Long> {

    @Query("select e from EventInstance e where e.eventHistory = ?1")
    Optional<EventInstance> findTopByEventHistory(EventHistory eventHistory);

    @Query("select e from EventInstance e, EventHistory eh where e.eventHistory = eh and e.eventTime = ?1 and eh.user = ?2")
    List<EventInstance> findAllByEventTimeAndUser(EventTime eventTime, User user);
}
