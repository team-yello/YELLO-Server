package com.yello.server.domain.notice.dto;

import static com.yello.server.global.common.factory.TimeFactory.toYearAndMonthFormattedString;

import com.yello.server.domain.notice.entity.Notice;
import lombok.Builder;

@Builder
public record NoticeDataResponse(
    String imageUrl,
    String redirectUrl,
    String startDate,
    String endDate,
    boolean isAvailable,
    String type,
    String title
) {

    public static NoticeDataResponse of(Notice notice, Boolean isAvailable) {
        return NoticeDataResponse.builder()
            .imageUrl(notice.getImageUrl())
            .redirectUrl(notice.getRedirectUrl())
            .startDate(toYearAndMonthFormattedString(notice.getStartDate().toLocalDateTime()))
            .endDate(toYearAndMonthFormattedString(notice.getEndDate().toLocalDateTime()))
            .isAvailable(isAvailable)
            .type(notice.getTag().getIntial())
            .title(notice.getTitle())
            .build();
    }

}
