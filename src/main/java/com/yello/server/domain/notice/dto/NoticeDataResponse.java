package com.yello.server.domain.notice.dto;

import com.yello.server.domain.notice.entity.Notice;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.yello.server.global.common.factory.TimeFactory.toYearAndMonthFormattedString;

@Builder
public record NoticeDataResponse(
        String imageUrl,
        String redirectUrl,
        String startDate,
        String endDate,
        boolean isAvailable
) {
    public static NoticeDataResponse of(Notice notice, Boolean isAvailable) {
        return NoticeDataResponse.builder()
                .imageUrl(notice.getImageUrl())
                .redirectUrl(notice.getRedirectUrl())
                .startDate(toYearAndMonthFormattedString(notice.getCreatedAt()))
                .endDate(toYearAndMonthFormattedString(notice.getEndDate().toLocalDateTime()))
                .isAvailable(isAvailable)
                .build();
    }

}
