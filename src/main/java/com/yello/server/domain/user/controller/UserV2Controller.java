package com.yello.server.domain.user.controller;

import static com.yello.server.global.common.SuccessCode.READ_USER_SUCCESS;

import com.yello.server.domain.user.dto.response.UserDetailV2Response;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.service.UserService;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
public class UserV2Controller {

    private final UserService userService;

    @GetMapping
    public BaseResponse<UserDetailV2Response> getUser(@AccessTokenUser User user) {
        val data = userService.getUserDetailV2(user.getId());
        return BaseResponse.success(READ_USER_SUCCESS, data);
    }
}
