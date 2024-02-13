package com.yello.server.global.common.factory;

import static com.yello.server.global.common.ErrorCode.IDEMPOTENCY_KEY_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.IDEMPOTENCY_KEY_INVALID_FORM_BAD_REQUEST_EXCEPTION;

import com.yello.server.domain.event.exception.EventBadRequestException;
import java.util.UUID;
import org.springframework.util.StringUtils;

public class UuidFactory {

    public static UUID checkUuid(String uuid) {
        UUID uuidIdempotencyKey;
        if (!StringUtils.hasText(uuid)) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_BAD_REQUEST_EXCEPTION);
        }
        try {
            uuidIdempotencyKey = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_INVALID_FORM_BAD_REQUEST_EXCEPTION);
        }
        return uuidIdempotencyKey;
    }

}
