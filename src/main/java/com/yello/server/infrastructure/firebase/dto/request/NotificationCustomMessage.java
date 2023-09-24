package com.yello.server.infrastructure.firebase.dto.request;

import java.util.List;

public record NotificationCustomMessage(
    List<Long> userIdList,
    String title,
    String message

) {

}
