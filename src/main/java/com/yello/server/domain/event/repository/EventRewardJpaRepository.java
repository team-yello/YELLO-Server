package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventReward;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventRewardJpaRepository extends JpaRepository<EventReward, Long> {

    @Query("select e from EventReward e where e.tag = ?1")
    Optional<EventReward> findByTag(String tag);

}
