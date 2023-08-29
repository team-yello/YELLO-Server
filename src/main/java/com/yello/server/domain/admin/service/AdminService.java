package com.yello.server.domain.admin.service;

import com.yello.server.domain.admin.dto.response.AdminCooldownContentVO;
import com.yello.server.domain.admin.dto.response.AdminCooldownResponse;
import com.yello.server.domain.admin.dto.response.AdminUserContentVO;
import com.yello.server.domain.admin.dto.response.AdminUserResponse;
import com.yello.server.domain.admin.repository.UserAdminRepository;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final CooldownRepository cooldownRepository;
    private final UserAdminRepository userAdminRepository;

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
