package com.yello.server.domain.event.dto.response;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventRewardRandomType;
import com.yello.server.domain.event.entity.EventRewardType;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.entity.EventType;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record EventResponse(
    EventType tag,
    String startDate,
    String endDate,
    String title,
    String subTitle,
    List<Object> animationList,
    List<EventResponse.EventRewardVO> eventReward
) {

    public static EventResponse of(Event event, List<EventTime> eventTimeList,
        List<EventRewardMapping> eventRewardMappingList)
        throws JsonProcessingException {
        // animation
        ObjectMapper objectMapper = new ObjectMapper();
        final List<Object> animationList = objectMapper.readValue(event.getAnimation(),
            new TypeReference<List<Object>>() {
            });

        List<EventResponse.EventRewardVO> eventRewardVOList = new ArrayList<>();
        for (EventTime eventTime : eventTimeList) {
            eventRewardVOList.add(
                EventRewardVO.of(eventTime, eventRewardMappingList)
            );
        }

        return EventResponse.builder()
            .tag(event.getTag())
            .startDate(event.getStartDate().format(ISO_OFFSET_DATE_TIME))
            .endDate(event.getEndDate().format(ISO_OFFSET_DATE_TIME))
            .title(event.getTitle())
            .subTitle(event.getSubTitle())
            .animationList(animationList)
            .eventReward(eventRewardVOList)
            .build();
    }

    @Builder
    public record EventRewardVO(
        OffsetTime startTime,
        OffsetTime endTime,
        Long rewardCount,
        List<EventResponse.EventRewardItemVO> eventRewardItem
    ) {

        public static EventRewardVO of(EventTime eventTime, List<EventRewardMapping> eventRewardMappingList) {
            List<EventResponse.EventRewardItemVO> eventRewardItemList = eventRewardMappingList.stream()
                .map(EventRewardItemVO::of).toList();

            return EventRewardVO.builder()
                .startTime(eventTime.getStartTime())
                .endTime(eventTime.getEndTime())
                .rewardCount(eventTime.getRewardCount())
                .eventRewardItem(eventRewardItemList)
                .build();
        }
    }

    @Builder
    public record EventRewardItemVO(
        EventRewardType tag,
        Integer eventRewardProbability,
        EventRewardRandomType randomTag
    ) {

        public static EventRewardItemVO of(EventRewardMapping eventRewardMapping) {
            return EventRewardItemVO.builder()
                .tag(eventRewardMapping.getEventReward().getTag())
                .eventRewardProbability(eventRewardMapping.getEventRewardProbability())
                .randomTag(eventRewardMapping.getRandomTag())
                .build();
        }
    }
}
