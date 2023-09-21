package com.yello.server.domain.admin.controller;

import static com.yello.server.global.common.SuccessCode.CREATE_VOTE_SUCCESS;
import static com.yello.server.global.common.SuccessCode.DELETE_COOLDOWN_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.DELETE_QUESTION_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.DELETE_USER_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.LOGIN_USER_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_COOLDOWN_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_QUESTION_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_QUESTION_DETAIL_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_USER_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_USER_DETAIL_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.UPDATE_USER_DETAIL_ADMIN_SUCCESS;
import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static com.yello.server.global.common.factory.PaginationFactory.createPageableLimitTen;

import com.yello.server.domain.admin.dto.request.AdminLoginRequest;
import com.yello.server.domain.admin.dto.request.AdminQuestionVoteRequest;
import com.yello.server.domain.admin.dto.request.AdminUserDetailRequest;
import com.yello.server.domain.admin.dto.response.AdminCooldownResponse;
import com.yello.server.domain.admin.dto.response.AdminLoginResponse;
import com.yello.server.domain.admin.dto.response.AdminQuestionDetailResponse;
import com.yello.server.domain.admin.dto.response.AdminQuestionResponse;
import com.yello.server.domain.admin.dto.response.AdminUserDetailResponse;
import com.yello.server.domain.admin.dto.response.AdminUserResponse;
import com.yello.server.domain.admin.service.AdminService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.global.common.dto.EmptyObject;
import com.yello.server.infrastructure.firebase.dto.request.NotificationCustomMessage;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final NotificationService notificationService;

    @PostMapping("/login")
    public BaseResponse<AdminLoginResponse> postAdminLogin(@RequestBody AdminLoginRequest request) {
        val data = adminService.login(request);
        return BaseResponse.success(LOGIN_USER_ADMIN_SUCCESS, data);
    }

    @GetMapping("/user")
    public BaseResponse<AdminUserResponse> getUserAdmin(@AccessTokenUser User user,
        @RequestParam Integer page,
        @Nullable @RequestParam String field,
        @Nullable @RequestParam String value) {
        val data = (field == null && value == null)
            ? adminService.findUser(user.getId(), createPageableLimitTen(page))
            : adminService.findUserContaining(user.getId(), createPageableLimitTen(page),
                field, value);
        return BaseResponse.success(READ_USER_ADMIN_SUCCESS, data);
    }

    @GetMapping("/user/{id}")
    public BaseResponse<AdminUserDetailResponse> getUserDetailAdmin(@AccessTokenUser User user,
        @PathVariable Long id) {
        val data = adminService.findUserDetail(user.getId(), id);
        return BaseResponse.success(READ_USER_DETAIL_ADMIN_SUCCESS, data);
    }

    @PostMapping("/user/{id}")
    public BaseResponse<EmptyObject> postUserDetailAdmin(@AccessTokenUser User user,
        @PathVariable Long id, @RequestBody AdminUserDetailRequest request) {
        val data = adminService.updateUserDetail(user.getId(), id, request);
        return BaseResponse.success(UPDATE_USER_DETAIL_ADMIN_SUCCESS);
    }

    @DeleteMapping("/user")
    public BaseResponse deleteUser(@AccessTokenUser User user, @RequestParam Long userId) {
        adminService.deleteUser(user.getId(), userId);
        return BaseResponse.success(DELETE_USER_ADMIN_SUCCESS);
    }

    @GetMapping("/cooldown")
    public BaseResponse<AdminCooldownResponse> getCooldownAdmin(@AccessTokenUser User user,
        @RequestParam Integer page,
        @Nullable @RequestParam String yelloId) {
        val data = yelloId == null
            ? adminService.findCooldown(user.getId(), createPageableLimitTen(page))
            : adminService.findCooldownContaining(user.getId(), createPageableLimitTen(page),
                yelloId);
        return BaseResponse.success(READ_COOLDOWN_ADMIN_SUCCESS, data);
    }

    @DeleteMapping("/cooldown")
    public BaseResponse deleteCooldown(@AccessTokenUser User user, @RequestParam Long cooldownId) {
        adminService.deleteCooldown(user.getId(), cooldownId);
        return BaseResponse.success(DELETE_COOLDOWN_ADMIN_SUCCESS);
    }

    @GetMapping("/question")
    public BaseResponse<AdminQuestionResponse> getQuestionAdmin(@AccessTokenUser User user,
        @RequestParam Integer page) {
        val data = adminService.findQuestion(user.getId(), createPageable(page, 20));
        return BaseResponse.success(READ_QUESTION_ADMIN_SUCCESS, data);
    }

    @GetMapping("/question/{id}")
    public BaseResponse<AdminQuestionDetailResponse> getQuestionDetailAdmin(
        @AccessTokenUser User user,
        @PathVariable Long id) {
        val data = adminService.findQuestionDetail(user.getId(), id);
        return BaseResponse.success(READ_QUESTION_DETAIL_ADMIN_SUCCESS, data);
    }

    @PostMapping("/question/{id}")
    public BaseResponse<EmptyObject> postQuestionSendAdmin(@AccessTokenUser User user,
        @PathVariable Long id, @RequestBody AdminQuestionVoteRequest request) {
        val data = adminService.createVote(user.getId(), id, request);
        data.forEach(notificationService::sendYelloNotification);

        return BaseResponse.success(CREATE_VOTE_SUCCESS, EmptyObject.builder().build());
    }

    @DeleteMapping("/question")
    public BaseResponse deleteQuestion(@AccessTokenUser User user, @RequestParam Long questionId) {
        adminService.deleteQuestion(user.getId(), questionId);
        return BaseResponse.success(DELETE_QUESTION_ADMIN_SUCCESS);
    }

    @PostMapping("/notification")
    public BaseResponse<EmptyObject> postCustomNotificationSendAdmin(@AccessTokenUser User user,
        @RequestBody NotificationCustomMessage request) {
        val data = notificationService.adminSendCustomNotification(user.getId(), request);

        return BaseResponse.success(CREATE_VOTE_SUCCESS, data);
    }
}
