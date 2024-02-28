package com.yello.server.domain.admin.dto.request;

public record AdminNoticeCreateRequest(
    String imageUrl,
    String redirectUrl,
    String startDate,
    String endDate,
    Boolean isAvailable,
    String tag,
    String title
) {

}
