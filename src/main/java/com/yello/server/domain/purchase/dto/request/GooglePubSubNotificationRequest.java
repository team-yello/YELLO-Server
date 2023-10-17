package com.yello.server.domain.purchase.dto.request;

import com.yello.server.domain.purchase.dto.request.google.GooglePubSubMessage;

public record GooglePubSubNotificationRequest(
    GooglePubSubMessage message,
    String subscription
) {

}
