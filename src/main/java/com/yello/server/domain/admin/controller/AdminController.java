package com.yello.server.domain.admin.controller;

import static com.yello.server.global.common.SuccessCode.CONFIGURATION_READ_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.CONFIGURATION_UPDATE_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.CREATE_VOTE_SUCCESS;
import static com.yello.server.global.common.SuccessCode.DELETE_COOLDOWN_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.DELETE_QUESTION_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.DELETE_USER_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.EVENT_CREATE_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.EVENT_REWARD_CREATE_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.LOGIN_USER_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.NOTICE_CREATE_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.NOTICE_READ_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.NOTICE_UPDATE_DETAIL_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_COOLDOWN_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_QUESTION_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_QUESTION_DETAIL_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_USER_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_USER_DETAIL_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.UPDATE_USER_DETAIL_ADMIN_SUCCESS;
import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static com.yello.server.global.common.factory.PaginationFactory.createPageableByNameSortDescLimitTen;
import static com.yello.server.global.common.factory.PaginationFactory.createPageableLimitTen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yello.server.domain.admin.dto.request.AdminEventCreateRequest;
import com.yello.server.domain.admin.dto.request.AdminEventRewardCreateRequest;
import com.yello.server.domain.admin.dto.request.AdminLoginRequest;
import com.yello.server.domain.admin.dto.request.AdminNoticeCreateRequest;
import com.yello.server.domain.admin.dto.request.AdminQuestionVoteRequest;
import com.yello.server.domain.admin.dto.request.AdminUserDetailRequest;
import com.yello.server.domain.admin.dto.response.AdminConfigurationResponse;
import com.yello.server.domain.admin.dto.response.AdminConfigurationUpdateRequest;
import com.yello.server.domain.admin.dto.response.AdminCooldownResponse;
import com.yello.server.domain.admin.dto.response.AdminLoginResponse;
import com.yello.server.domain.admin.dto.response.AdminQuestionDetailResponse;
import com.yello.server.domain.admin.dto.response.AdminQuestionResponse;
import com.yello.server.domain.admin.dto.response.AdminUserDetailResponse;
import com.yello.server.domain.admin.dto.response.AdminUserResponse;
import com.yello.server.domain.admin.entity.AdminConfigurationType;
import com.yello.server.domain.admin.service.AdminService;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.global.common.dto.EmptyObject;
import com.yello.server.infrastructure.firebase.dto.request.NotificationCustomMessage;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import java.util.List;
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
        @RequestParam(value = "page") Integer page,
        @RequestParam(value = "field", required = false) String field,
        @RequestParam(value = "value", required = false) String value) {
        val data = (field == null && value == null)
            ? adminService.findUser(user.getId(), createPageableByNameSortDescLimitTen(page))
            : adminService.findUserContaining(user.getId(),
                createPageableByNameSortDescLimitTen(page),
                field, value);
        return BaseResponse.success(READ_USER_ADMIN_SUCCESS, data);
    }

    @GetMapping("/user/{id}")
    public BaseResponse<AdminUserDetailResponse> getUserDetailAdmin(@AccessTokenUser User user,
        @PathVariable(value = "id") Long id) {
        val data = adminService.findUserDetail(user.getId(), id);
        return BaseResponse.success(READ_USER_DETAIL_ADMIN_SUCCESS, data);
    }

    @PostMapping("/user/{id}")
    public BaseResponse<EmptyObject> postUserDetailAdmin(@AccessTokenUser User user,
        @PathVariable(value = "id") Long id, @RequestBody AdminUserDetailRequest request) {
        val data = adminService.updateUserDetail(user.getId(), id, request);
        return BaseResponse.success(UPDATE_USER_DETAIL_ADMIN_SUCCESS);
    }

    @DeleteMapping("/user")
    public BaseResponse deleteUser(@AccessTokenUser User user, @RequestParam(value = "userId") Long userId) {
        adminService.deleteUser(user.getId(), userId);
        return BaseResponse.success(DELETE_USER_ADMIN_SUCCESS);
    }

    @GetMapping("/cooldown")
    public BaseResponse<AdminCooldownResponse> getCooldownAdmin(@AccessTokenUser User user,
        @RequestParam(value = "page") Integer page,
        @RequestParam(value = "yelloId", required = false) String yelloId) {
        val data = yelloId == null
            ? adminService.findCooldown(user.getId(), createPageableLimitTen(page))
            : adminService.findCooldownContaining(user.getId(), createPageableLimitTen(page),
                yelloId);
        return BaseResponse.success(READ_COOLDOWN_ADMIN_SUCCESS, data);
    }

    @DeleteMapping("/cooldown")
    public BaseResponse deleteCooldown(@AccessTokenUser User user,
        @RequestParam(value = "cooldownId") Long cooldownId) {
        adminService.deleteCooldown(user.getId(), cooldownId);
        return BaseResponse.success(DELETE_COOLDOWN_ADMIN_SUCCESS);
    }

    @GetMapping("/question")
    public BaseResponse<AdminQuestionResponse> getQuestionAdmin(@AccessTokenUser User user,
        @RequestParam(value = "page") Integer page) {
        val data = adminService.findQuestion(user.getId(), createPageable(page, 20));
        return BaseResponse.success(READ_QUESTION_ADMIN_SUCCESS, data);
    }

    @GetMapping("/question/{id}")
    public BaseResponse<AdminQuestionDetailResponse> getQuestionDetailAdmin(
        @AccessTokenUser User user,
        @PathVariable(value = "id") Long id) {
        val data = adminService.findQuestionDetail(user.getId(), id);
        return BaseResponse.success(READ_QUESTION_DETAIL_ADMIN_SUCCESS, data);
    }

    @PostMapping("/question/{id}")
    public BaseResponse<EmptyObject> postQuestionSendAdmin(@AccessTokenUser User user,
        @PathVariable(value = "id") Long id, @RequestBody AdminQuestionVoteRequest request) {
        val data = adminService.createVote(user.getId(), id, request);
        data.forEach(notificationService::sendYelloNotification);

        return BaseResponse.success(CREATE_VOTE_SUCCESS, EmptyObject.builder().build());
    }

    @DeleteMapping("/question")
    public BaseResponse deleteQuestion(@AccessTokenUser User user,
        @RequestParam(value = "questionId") Long questionId) {
        adminService.deleteQuestion(user.getId(), questionId);
        return BaseResponse.success(DELETE_QUESTION_ADMIN_SUCCESS);
    }

    @PostMapping("/notification")
    public BaseResponse<EmptyObject> postCustomNotificationSendAdmin(@AccessTokenUser User user,
        @RequestBody NotificationCustomMessage request) {
        val data = notificationService.adminSendCustomNotification(user.getId(), request);

        return BaseResponse.success(CREATE_VOTE_SUCCESS, data);
    }

    @GetMapping("/configuration")
    public BaseResponse<AdminConfigurationResponse> getConfigurations(@RequestParam(value = "tag") String tag,
        @AccessTokenUser User user) {
        final AdminConfigurationType configurationType = AdminConfigurationType.fromCode(tag);
        val data = adminService.getConfigurations(user.getId(), configurationType);
        return BaseResponse.success(CONFIGURATION_READ_ADMIN_SUCCESS, null);
    }

    @PostMapping("/configuration")
    public BaseResponse<EmptyObject> postConfigurations(
        @RequestBody AdminConfigurationUpdateRequest request,
        @AccessTokenUser User user) {
        final AdminConfigurationType configurationType = AdminConfigurationType.fromCode(request.tag());

        val data = adminService.updateConfigurations(user.getId(), configurationType, request.value());

        return BaseResponse.success(CONFIGURATION_UPDATE_ADMIN_SUCCESS, data);
    }

    @GetMapping("/notice")
    public BaseResponse<List<Notice>> getNotices(@AccessTokenUser User user) {
        val data = adminService.getNotices(user.getId());
        return BaseResponse.success(NOTICE_READ_ADMIN_SUCCESS, data);
    }

    @PostMapping("/notice")
    public BaseResponse<EmptyObject> createNotice(@AccessTokenUser User user,
        @RequestBody AdminNoticeCreateRequest request) {
        val data = adminService.createNotice(user.getId(), request);
        return BaseResponse.success(NOTICE_CREATE_ADMIN_SUCCESS, data);
    }

    @PostMapping("/notice/{id}")
    public BaseResponse<EmptyObject> updateNotice(@AccessTokenUser User user, @PathVariable(value = "id") Long noticeId,
        @RequestBody AdminNoticeCreateRequest request) {
        val data = adminService.updateNotice(user.getId(), noticeId, request);
        return BaseResponse.success(NOTICE_UPDATE_DETAIL_ADMIN_SUCCESS, data);
    }

    @PostMapping("/event")
    public BaseResponse<EmptyObject> createEvent(@AccessTokenUser User user,
        @RequestBody AdminEventCreateRequest request) throws JsonProcessingException {
        val data = adminService.createEvent(user.getId(), request);
        return BaseResponse.success(EVENT_CREATE_ADMIN_SUCCESS, data);
    }

    @PostMapping("/event/reward")
    public BaseResponse<EmptyObject> createEventReward(@AccessTokenUser User user,
        @RequestBody AdminEventRewardCreateRequest request) {
        val data = adminService.createEventReward(user.getId(), request);
        return BaseResponse.success(EVENT_REWARD_CREATE_ADMIN_SUCCESS, data);
    }
}
