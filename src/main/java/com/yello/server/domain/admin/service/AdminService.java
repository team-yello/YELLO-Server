package com.yello.server.domain.admin.service;

import static com.yello.server.global.common.ErrorCode.DEVICE_TOKEN_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USER_ADMIN_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;

import com.yello.server.domain.admin.dto.request.AdminLoginRequest;
import com.yello.server.domain.admin.dto.request.AdminUserDetailRequest;
import com.yello.server.domain.admin.dto.response.AdminCooldownContentVO;
import com.yello.server.domain.admin.dto.response.AdminCooldownResponse;
import com.yello.server.domain.admin.dto.response.AdminLoginResponse;
import com.yello.server.domain.admin.dto.response.AdminUserContentVO;
import com.yello.server.domain.admin.dto.response.AdminUserDetailResponse;
import com.yello.server.domain.admin.dto.response.AdminUserResponse;
import com.yello.server.domain.admin.exception.UserAdminNotFoundException;
import com.yello.server.domain.admin.repository.UserAdminRepository;
import com.yello.server.domain.authorization.service.TokenProvider;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.global.common.dto.EmptyObject;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final UserManager userManager;
    private final TokenProvider tokenProvider;
    private final CooldownRepository cooldownRepository;
    private final UserAdminRepository userAdminRepository;

    @Value("${admin.password}")
    String adminPassword;

    public AdminLoginResponse login(AdminLoginRequest request) {
        String accessToken;

        if (request.password().equals(adminPassword)) {
            final User user = userManager.getOfficialUser(Gender.FEMALE);

            accessToken = tokenProvider.createAccessToken(user.getId(), user.getUuid());
        } else {
            throw new UserAdminNotFoundException(USER_ADMIN_NOT_FOUND_EXCEPTION);
        }

        return AdminLoginResponse.of(accessToken);
    }

    public AdminUserResponse findUser(Long adminId, Pageable page) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final Long totalCount = userRepository.count();
        final List<AdminUserContentVO> list = userRepository.findAll(page).stream()
            .map(AdminUserContentVO::of)
            .toList();

        return AdminUserResponse.of(totalCount, list);
    }

    public AdminUserResponse findUserContaining(Long adminId, Pageable page, String yelloId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final Long totalCount = userRepository.countAllByYelloIdContaining(yelloId);
        final List<AdminUserContentVO> list = userRepository.findAllContaining(page, yelloId).stream()
            .map(AdminUserContentVO::of)
            .toList();

        return AdminUserResponse.of(totalCount, list);
    }

    public AdminUserDetailResponse findUserDetail(Long adminId, Long userId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final User user = userRepository.getById(userId);

        return AdminUserDetailResponse.of(user);
    }

    @Transactional
    public EmptyObject updateUserDetail(Long adminId, Long userId, AdminUserDetailRequest request) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final User user = userRepository.getById(userId);

        userRepository.findByUuidNotFiltered(request.uuid())
            .ifPresent(action -> {
                throw new UserConflictException(UUID_CONFLICT_USER_EXCEPTION);
            });
        userRepository.findByYelloIdNotFiltered(request.yelloId())
            .ifPresent(action -> {
                throw new UserConflictException(YELLOID_CONFLICT_USER_EXCEPTION);
            });
        userRepository.findByDeviceTokenNotFiltered(request.deviceToken())
            .ifPresent(action -> {
                throw new UserConflictException(DEVICE_TOKEN_CONFLICT_USER_EXCEPTION);
            });

        user.update(request);

        return EmptyObject.builder().build();
    }

    @Transactional
    public void deleteUser(Long adminId, Long userId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);
        final User user = userRepository.getById(userId);

        // logic
        userRepository.delete(user);
    }

    public AdminCooldownResponse findCooldown(Long adminId, Pageable page) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final Long totalCount = cooldownRepository.count();
        final List<AdminCooldownContentVO> list = cooldownRepository.findAll(page).stream()
            .map(AdminCooldownContentVO::of)
            .toList();

        return AdminCooldownResponse.of(totalCount, list);
    }

    public AdminCooldownResponse findCooldownContaining(Long adminId, Pageable page, String yelloId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final Long totalCount = cooldownRepository.countAllByYelloIdContaining(yelloId);
        final List<AdminCooldownContentVO> list = cooldownRepository.findAllContaining(page, yelloId).stream()
            .map(AdminCooldownContentVO::of)
            .toList();

        return AdminCooldownResponse.of(totalCount, list);
    }

    @Transactional
    public void deleteCooldown(Long adminId, Long cooldownId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);
        final Cooldown cooldown = cooldownRepository.getById(cooldownId);

        // logic
        cooldownRepository.delete(cooldown);
    }
}
