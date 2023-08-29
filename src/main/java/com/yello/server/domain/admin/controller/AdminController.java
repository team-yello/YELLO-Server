package com.yello.server.domain.admin.controller;

import static com.yello.server.global.common.SuccessCode.DELETE_COOLDOWN_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.DELETE_USER_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_COOLDOWN_ADMIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_USER_ADMIN_SUCCESS;

import com.yello.server.domain.admin.dto.response.AdminCooldownResponse;
import com.yello.server.domain.admin.dto.response.AdminUserResponse;
import com.yello.server.domain.admin.service.AdminService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.global.common.factory.PaginationFactory;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/user")
    public BaseResponse<AdminUserResponse> getUserAdmin(@AccessTokenUser User user, @RequestParam Integer page,
        @Nullable @RequestParam String yelloId) {
        val data = yelloId == null
            ? adminService.findUser(user.getId(), PaginationFactory.createPageableLimitTen(page))
            : adminService.findUserContaining(user.getId(), PaginationFactory.createPageableLimitTen(page),
                yelloId);
        return BaseResponse.success(READ_USER_ADMIN_SUCCESS, data);
    }

    @DeleteMapping("/user")
    public BaseResponse deleteUser(@AccessTokenUser User user, @RequestParam Long userId) {
        adminService.deleteUser(user.getId(), userId);
        return BaseResponse.success(DELETE_USER_ADMIN_SUCCESS);
    }

    @GetMapping("/cooldown")
    public BaseResponse<AdminCooldownResponse> getCooldownAdmin(@AccessTokenUser User user, @RequestParam Integer page,
        @Nullable @RequestParam String yelloId) {
        val data = yelloId == null
            ? adminService.findCooldown(user.getId(), PaginationFactory.createPageableLimitTen(page))
            : adminService.findCooldownContaining(user.getId(), PaginationFactory.createPageableLimitTen(page),
                yelloId);
        return BaseResponse.success(READ_COOLDOWN_ADMIN_SUCCESS, data);
    }

    @DeleteMapping("/cooldown")
    public BaseResponse deleteCooldown(@AccessTokenUser User user, @RequestParam Long cooldownId) {
        adminService.deleteCooldown(user.getId(), cooldownId);
        return BaseResponse.success(DELETE_COOLDOWN_ADMIN_SUCCESS);
    }
}
