package com.yello.server.domain.notice.controller;


import com.yello.server.domain.notice.dto.NoticeDataResponse;
import com.yello.server.domain.notice.entity.NoticeType;
import com.yello.server.domain.notice.service.NoticeService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.yello.server.global.common.SuccessCode.READ_NOTICE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/notice/{tag}")
    public BaseResponse<NoticeDataResponse> findNotice(@AccessTokenUser User user, @PathVariable String tag) {
        val data = noticeService.findNotice(user.getId(), tag);
        return BaseResponse.success(READ_NOTICE_SUCCESS, data);
    }
}
