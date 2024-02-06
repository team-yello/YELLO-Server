package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import java.util.List;

public interface EventRepository {

    List<Event> findAll();

    List<EventTime> findAllByEventId(Long eventId);

    List<EventRewardMapping> findAllByEventTimeId(Long eventTimeId);

    List<EventReward> findRewardAll();

    EventReward getRewardById(Long eventRewardId);
}
