package com.yello.server.domain.user.controller;

import static com.yello.server.global.common.SuccessCode.DELETE_USER_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_USER_SUBSCRIBE_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_USER_SUCCESS;
import static com.yello.server.global.common.SuccessCode.UPDATE_DEVICE_TOKEN_USER_SUCCESS;

import com.yello.server.domain.user.dto.request.UserDeleteReasonRequest;
import com.yello.server.domain.user.dto.request.UserDeviceTokenRequest;
import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserDetailV2Response;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.dto.response.UserSubscribeDetailResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.service.UserService;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.global.common.dto.EmptyObject;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/v1/user")
    public BaseResponse<UserDetailResponse> findUser(@AccessTokenUser User user) {
        val data = userService.findMyProfile(user.getId());
        return BaseResponse.success(READ_USER_SUCCESS, data);
    }

    @GetMapping("/v2/user")
    public BaseResponse<UserDetailV2Response> getUser(@AccessTokenUser User user) {
        val data = userService.getUserDetailV2(user.getId());
        return BaseResponse.success(READ_USER_SUCCESS, data);
    }

    @GetMapping("/v1/user/{userId}")
    public BaseResponse<UserResponse> findUserById(@PathVariable Long userId) {
        val data = userService.findUserById(userId);
        return BaseResponse.success(READ_USER_SUCCESS, data);
    }

    @PutMapping("/v1/user/device")
    public BaseResponse<EmptyObject> putUserDeviceToken(
        @AccessTokenUser User user,
        @RequestBody UserDeviceTokenRequest request
    ) {
        val data = userService.updateUserDeviceToken(user, request);
        return BaseResponse.success(UPDATE_DEVICE_TOKEN_USER_SUCCESS, data);
    }

    @DeleteMapping("/v1/user")
    public BaseResponse deleteUser(@AccessTokenUser User user) {
        userService.delete(user);
        return BaseResponse.success(DELETE_USER_SUCCESS);
    }

    @GetMapping("/v1/user/subscribe")
    public BaseResponse<UserSubscribeDetailResponse> getUserSubscribe(@AccessTokenUser User user) {
        val data = userService.getUserSubscribe(user.getId());
        return BaseResponse.success(READ_USER_SUBSCRIBE_SUCCESS, data);
    }

    @DeleteMapping("/v2/user")
    public BaseResponse deleteUserWithReason(@AccessTokenUser User user, @RequestBody
    UserDeleteReasonRequest request) {
        userService.deleteUserWithReason(user.getId(), request);
        return BaseResponse.success(DELETE_USER_SUCCESS);
    }
}
