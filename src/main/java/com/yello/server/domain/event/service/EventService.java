package com.yello.server.domain.event.service;

import static com.yello.server.global.common.util.ConstantUtil.GlobalZoneId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yello.server.domain.event.dto.response.EventResponse;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.repository.EventRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<EventResponse> getEvents(Long userId) throws JsonProcessingException {
        // exception
        final User user = userRepository.getById(userId);

        // logic
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        List<EventResponse> result = new ArrayList<>();

        final List<Event> eventList = eventRepository.findAll().stream()
            .filter(event -> now.isAfter(event.getStartDate()) && now.isBefore(event.getEndDate()))
            .toList();

        for (Event event : eventList) {
            final List<EventTime> eventTimeList = eventRepository.findAllByEventId(event.getId());
            Long eventTimeId = 0L;
            for (EventTime eventTime : eventTimeList) {
                if (eventTime.getEvent().equals(event)) {
                    eventTimeId = eventTime.getId();
                }
            }
            final List<EventRewardMapping> eventRewardMappingList = new ArrayList<>(
                eventRepository.findAllByEventTimeId(
                    eventTimeId));

            if (!eventTimeList.isEmpty()) {
                result.add(EventResponse.of(event, eventTimeList, eventRewardMappingList));
            }
        }

        return result;
    }
}
