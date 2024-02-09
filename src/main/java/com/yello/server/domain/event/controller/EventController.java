package com.yello.server.domain.event.controller;

import static com.yello.server.global.common.ErrorCode.ADMOB_URI_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.IDEMPOTENCY_KEY_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.IDEMPOTENCY_KEY_INVALID_FORM_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.SuccessCode.EVENT_JOIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.EVENT_NOTICE_SUCCESS;
import static com.yello.server.global.common.SuccessCode.EVENT_REWARD_SUCCESS;
import static com.yello.server.global.common.SuccessCode.VERIFY_ADMOB_SSV_SUCCESS;
import static com.yello.server.global.common.util.ConstantUtil.IdempotencyKeyHeader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yello.server.domain.event.dto.request.EventJoinRequest;
import com.yello.server.domain.event.dto.response.EventResponse;
import com.yello.server.domain.event.dto.response.EventRewardResponse;
import com.yello.server.domain.event.exception.EventBadRequestException;
import com.yello.server.domain.event.service.EventService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    @GetMapping("/v1/event")
    public BaseResponse<List<EventResponse>> getEvents(@AccessTokenUser User user)
        throws JsonProcessingException {
        val data = eventService.getEvents(user.getId());
        return BaseResponse.success(EVENT_NOTICE_SUCCESS, data);
    }

    @PostMapping("/v1/event")
    public BaseResponse joinEvent(@AccessTokenUser User user, HttpServletRequest requestServlet,
        @RequestBody EventJoinRequest request) {
        final String idempotencyKey = requestServlet.getHeader(IdempotencyKeyHeader);
        if (!StringUtils.hasText(idempotencyKey)) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_BAD_REQUEST_EXCEPTION);
        }

        UUID uuidIdempotencyKey;
        try {
            uuidIdempotencyKey = UUID.fromString(idempotencyKey);
        } catch (IllegalArgumentException e) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_INVALID_FORM_BAD_REQUEST_EXCEPTION);
        }

        eventService.joinEvent(user.getId(), uuidIdempotencyKey, request);
        return BaseResponse.success(EVENT_JOIN_SUCCESS);
    }

    @PostMapping("/v1/event/reward")
    public BaseResponse<EventRewardResponse> rewardEvent(@AccessTokenUser User user,
        HttpServletRequest requestServlet) throws JsonProcessingException {
        final String idempotencyKey = requestServlet.getHeader(IdempotencyKeyHeader);
        if (!StringUtils.hasText(idempotencyKey)) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_BAD_REQUEST_EXCEPTION);
        }

        UUID uuidIdempotencyKey;
        try {
            uuidIdempotencyKey = UUID.fromString(idempotencyKey);
        } catch (IllegalArgumentException e) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_INVALID_FORM_BAD_REQUEST_EXCEPTION);
        }

        val data = eventService.rewardEvent(user.getId(), uuidIdempotencyKey);
        return BaseResponse.success(EVENT_REWARD_SUCCESS, data);
    }

    @GetMapping("/v1/admob/verify")
    public BaseResponse verifyAdmobReward(HttpServletRequest request) {
        URI uri;
        try {
            uri =
                new URI(request.getScheme(), null, request.getServerName(), request.getServerPort(),
                    request.getRequestURI(), request.getQueryString(), null);
        } catch (URISyntaxException e) {
            throw new EventBadRequestException(ADMOB_URI_BAD_REQUEST_EXCEPTION);
        }

        eventService.verifyAdmobReward(uri, request);

        return BaseResponse.success(VERIFY_ADMOB_SSV_SUCCESS);
    }
}
