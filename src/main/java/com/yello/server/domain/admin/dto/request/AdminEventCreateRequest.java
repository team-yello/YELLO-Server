package com.yello.server.domain.admin.dto.request;

import com.yello.server.domain.event.entity.EventRewardRandomType;
import com.yello.server.domain.event.entity.EventType;
import java.time.OffsetTime;
import java.util.List;

public record AdminEventCreateRequest(
    EventType tag,
    String startDate,
    String endDate,
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
        String tag,
        Integer eventRewardProbability,
        EventRewardRandomType randomTag
    ) {

    }
}
