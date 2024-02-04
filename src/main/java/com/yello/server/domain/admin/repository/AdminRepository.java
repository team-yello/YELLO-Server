package com.yello.server.domain.admin.repository;

import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventRewardType;
import com.yello.server.domain.event.entity.EventTime;

public interface AdminRepository {

    Event save(Event newEvent);

    EventTime save(EventTime newEventTime);

    EventRewardMapping save(EventRewardMapping newEventRewardMapping);

    Event getById(Long eventId);

    EventTime getEventTimeById(Long eventTimeId);

    EventReward getByTag(EventRewardType tag);
}
