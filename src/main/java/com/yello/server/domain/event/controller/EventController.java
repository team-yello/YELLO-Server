package com.yello.server.domain.event.controller;

import static com.yello.server.global.common.SuccessCode.EVENT_NOTICE_SUCCESS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yello.server.domain.event.dto.response.EventResponse;
import com.yello.server.domain.event.service.EventService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    @GetMapping("/v1/event")
    public BaseResponse<List<EventResponse>> getEvents(@AccessTokenUser User user) throws JsonProcessingException {
        val data = eventService.getEvents(user.getId());
        return BaseResponse.success(EVENT_NOTICE_SUCCESS, data);
    }
}
