package com.yello.server.domain.purchase.dto.request.google;

import java.util.Map;
import java.util.Optional;

public record GooglePubSubMessage(
    Optional<Map<String, String>> attributes,
    String data,
    Long messageId,
    Optional<Long> message_id,
    Optional<String> publishTime,
    Optional<String> publish_time
) {

}
