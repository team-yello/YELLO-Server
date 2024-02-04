package com.yello.server.domain.admin.dto.request;

import com.yello.server.domain.event.entity.EventRewardRandomType;
import com.yello.server.domain.event.entity.EventRewardType;
import com.yello.server.domain.event.entity.EventType;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.List;

public record AdminEventCreateRequest(
    EventType tag,
    ZonedDateTime startDate,
    ZonedDateTime endDate,
    String title,
    String subTitle,
    List<Object> animationList,
    List<EventRewardVO> eventReward
) {

    public record EventRewardVO(
        OffsetTime startTime,
        OffsetTime endTime,
        Long rewardCount,
        List<EventRewardItemVO> eventRewardItem
    ) {

    }

    public record EventRewardItemVO(
        EventRewardType tag,
        Integer eventRewardProbability,
        EventRewardRandomType randomTag
    ) {

    }
}
