package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventHistory;
import com.yello.server.domain.event.entity.EventInstance;
import com.yello.server.domain.event.entity.EventInstanceReward;
import com.yello.server.domain.event.entity.EventReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.entity.EventType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    EventHistory save(EventHistory newEventHistory);

    EventInstance save(EventInstance newEventInstance);

    EventInstanceReward save(EventInstanceReward newEventInstanceReward);

    List<Event> findAll();

    List<EventTime> findAllByEventId(Long eventId);

    List<EventRewardMapping> findAllByEventTimeId(Long eventTimeId);

    List<EventReward> findRewardAll();

    Event getByTag(EventType tag);

    EventReward getRewardById(Long eventRewardId);

    Optional<EventHistory> findHistoryByIdempotencyKey(UUID idempotencyKey);

    Optional<EventInstance> findInstanceByEventHistory(EventHistory eventHistory);
}
