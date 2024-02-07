package com.yello.server.domain.event.dto.response;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.entity.EventType;
import jakarta.annotation.Nullable;
import java.time.OffsetTime;
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
    @Nullable EventResponse.EventRewardVO eventReward
) {

    public static EventResponse of(Event event, @Nullable EventTime eventTime,
        @Nullable List<EventRewardMapping> eventRewardMappingList)
        throws JsonProcessingException {
        // animation
        ObjectMapper objectMapper = new ObjectMapper();
        final List<Object> animationList = objectMapper.readValue(event.getAnimation(),
            new TypeReference<List<Object>>() {
            });

        EventResponse.EventRewardVO eventRewardVO = eventTime == null || eventRewardMappingList == null ? null
            : EventResponse.EventRewardVO.of(eventTime, eventRewardMappingList);

        return EventResponse.builder()
            .tag(event.getTag())
            .startDate(event.getStartDate().format(ISO_OFFSET_DATE_TIME))
            .endDate(event.getEndDate().format(ISO_OFFSET_DATE_TIME))
            .title(event.getTitle())
            .subTitle(event.getSubTitle())
            .animationList(animationList)
            .eventReward(eventRewardVO)
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
        String tag,
        String eventRewardTitle,
        String eventRewardImage,
        Long maxRewardValue,
        Long minRewardValue,
        Integer eventRewardProbability,
        String randomTag
    ) {

        public static EventRewardItemVO of(EventRewardMapping eventRewardMapping) {
            return EventRewardItemVO.builder()
                .tag(eventRewardMapping.getEventReward().getTag())
                .eventRewardTitle(eventRewardMapping.getEventReward().getTitle())
                .eventRewardImage(eventRewardMapping.getEventReward().getImage())
                .maxRewardValue(eventRewardMapping.getEventReward().getMaxRewardValue())
                .minRewardValue(eventRewardMapping.getEventReward().getMinRewardValue())
                .eventRewardProbability(eventRewardMapping.getEventRewardProbability())
                .randomTag(eventRewardMapping.getEventRandom().getRandomTag())
                .build();
        }
    }
}
