package com.yello.server.domain.event.controller;

import static com.yello.server.global.common.ErrorCode.ADMOB_URI_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EMPTY_QUERY_STRING_EXCEPTION;
import static com.yello.server.global.common.SuccessCode.EVENT_JOIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.EVENT_NOTICE_SUCCESS;
import static com.yello.server.global.common.SuccessCode.EVENT_REWARD_SUCCESS;
import static com.yello.server.global.common.SuccessCode.GET_IS_POSSIBLE_ADMOB_SUCCESS;
import static com.yello.server.global.common.SuccessCode.REWARD_ADMOB_SUCCESS;
import static com.yello.server.global.common.util.ConstantUtil.IdempotencyKeyHeader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yello.server.domain.event.dto.request.AdmobRewardRequest;
import com.yello.server.domain.event.dto.request.EventJoinRequest;
import com.yello.server.domain.event.dto.response.EventResponse;
import com.yello.server.domain.event.dto.response.EventRewardResponse;
import com.yello.server.domain.event.dto.response.GetIsPossibleAdmob;
import com.yello.server.domain.event.exception.EventBadRequestException;
import com.yello.server.domain.event.exception.EventNotFoundException;
import com.yello.server.domain.event.service.EventService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.global.common.factory.UuidFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        UUID uuidIdempotencyKey =
            UuidFactory.checkUuid(requestServlet.getHeader(IdempotencyKeyHeader));
        eventService.joinEvent(user.getId(), uuidIdempotencyKey, request);
        return BaseResponse.success(EVENT_JOIN_SUCCESS);
    }

    @PostMapping("/v1/event/reward")
    public BaseResponse<EventRewardResponse> rewardEvent(@AccessTokenUser User user,
        HttpServletRequest requestServlet) throws JsonProcessingException {
        UUID uuidIdempotencyKey =
            UuidFactory.checkUuid(requestServlet.getHeader(IdempotencyKeyHeader));
        val data = eventService.rewardEvent(user.getId(), uuidIdempotencyKey);
        return BaseResponse.success(EVENT_REWARD_SUCCESS, data);
    }

    @GetMapping("/v1/admob/verify")
    public ResponseEntity<?> verifyAdmob(HttpServletRequest request) {
        URI uri;
        System.out.println(request.getQueryString() + " alalalalalal");
        if (request.getQueryString()==null) {
            throw new EventNotFoundException(EMPTY_QUERY_STRING_EXCEPTION);
        }

        try {
            uri =
                new URI(request.getScheme(), null, request.getServerName(), request.getServerPort(),
                    request.getRequestURI(), request.getQueryString(), null);
        } catch (URISyntaxException e) {
            throw new EventBadRequestException(ADMOB_URI_BAD_REQUEST_EXCEPTION);
        }
        System.out.println("URIIIII : " + uri);
        eventService.verifyAdmobReward(uri, request);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/v1/admob/reward")
    public BaseResponse<EventRewardResponse> rewardAdmob(@AccessTokenUser User user,
        @RequestBody AdmobRewardRequest request) {
        val data = eventService.rewardAdmob(user.getId(), request);
        return BaseResponse.success(REWARD_ADMOB_SUCCESS, data);
    }

    @GetMapping("/v1/admob/possible/{tag}")
    public BaseResponse<GetIsPossibleAdmob> getIsPossibleAdmob(@AccessTokenUser User user,
        @PathVariable("tag") String tag) {
        val data = eventService.getIsPossibleAdmob(user.getId(), tag);
        return BaseResponse.success(GET_IS_POSSIBLE_ADMOB_SUCCESS, data);
    }
}
