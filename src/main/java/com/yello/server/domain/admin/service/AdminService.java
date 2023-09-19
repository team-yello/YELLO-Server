package com.yello.server.domain.admin.service;

import static com.yello.server.global.common.ErrorCode.DEVICE_TOKEN_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USER_ADMIN_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USER_ADMIN_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;

import com.yello.server.domain.admin.dto.request.AdminLoginRequest;
import com.yello.server.domain.admin.dto.request.AdminQuestionVoteRequest;
import com.yello.server.domain.admin.dto.request.AdminUserDetailRequest;
import com.yello.server.domain.admin.dto.response.AdminCooldownContentVO;
import com.yello.server.domain.admin.dto.response.AdminCooldownResponse;
import com.yello.server.domain.admin.dto.response.AdminLoginResponse;
import com.yello.server.domain.admin.dto.response.AdminQuestionContentVO;
import com.yello.server.domain.admin.dto.response.AdminQuestionDetailResponse;
import com.yello.server.domain.admin.dto.response.AdminQuestionResponse;
import com.yello.server.domain.admin.dto.response.AdminUserContentVO;
import com.yello.server.domain.admin.dto.response.AdminUserDetailResponse;
import com.yello.server.domain.admin.dto.response.AdminUserResponse;
import com.yello.server.domain.admin.exception.UserAdminBadRequestException;
import com.yello.server.domain.admin.exception.UserAdminNotFoundException;
import com.yello.server.domain.admin.repository.UserAdminRepository;
import com.yello.server.domain.authorization.service.TokenProvider;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.global.common.dto.EmptyObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private final QuestionRepository questionRepository;
    private final VoteRepository voteRepository;
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

    public AdminUserResponse findUserContaining(Long adminId, Pageable page, String field,
        String value) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        Long totalCount = 0L;
        List<AdminUserContentVO> list = new ArrayList<>();

        if (field==null || value==null) {
            throw new UserAdminBadRequestException(USER_ADMIN_BAD_REQUEST_EXCEPTION);
        } else if (field.equals("yelloId")) {
            totalCount = userRepository.countAllByYelloIdContaining(value);
            list = userRepository.findAllByYelloIdContaining(page, value).stream()
                .map(AdminUserContentVO::of)
                .toList();
        } else if (field.equals("name")) {
            totalCount = userRepository.countAllByNameContaining(value);
            list = userRepository.findAllByNameContaining(page, value).stream()
                .map(AdminUserContentVO::of)
                .toList();
        } else {
            throw new UserAdminBadRequestException(USER_ADMIN_BAD_REQUEST_EXCEPTION);
        }

        return AdminUserResponse.of(totalCount, list);
    }

    public AdminUserDetailResponse findUserDetail(Long adminId, Long userId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final User user = userRepository.getByIdNotFiltered(userId);

        return AdminUserDetailResponse.of(user);
    }

    @Transactional
    public EmptyObject updateUserDetail(Long adminId, Long userId, AdminUserDetailRequest request) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final User user = userRepository.getByIdNotFiltered(userId);

        userRepository.findByUuidNotFiltered(request.uuid())
            .ifPresent(action -> {
                if (!Objects.equals(action.getId(), userId)) {
                    throw new UserConflictException(UUID_CONFLICT_USER_EXCEPTION);
                }
            });
        userRepository.findByYelloIdNotFiltered(request.yelloId())
            .ifPresent(action -> {
                if (!Objects.equals(action.getId(), userId)) {
                    throw new UserConflictException(YELLOID_CONFLICT_USER_EXCEPTION);
                }
            });
        userRepository.findByDeviceTokenNotFiltered(request.deviceToken())
            .ifPresent(action -> {
                if (!Objects.equals(action.getId(), userId)) {
                    throw new UserConflictException(DEVICE_TOKEN_CONFLICT_USER_EXCEPTION);
                }
            });

        user.update(request);

        return EmptyObject.builder().build();
    }

    @Transactional
    public void deleteUser(Long adminId, Long userId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);
        final User user = userRepository.getByIdNotFiltered(userId);

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

    public AdminCooldownResponse findCooldownContaining(Long adminId, Pageable page,
        String yelloId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final Long totalCount = cooldownRepository.countAllByYelloIdContaining(yelloId);
        final List<AdminCooldownContentVO> list =
            cooldownRepository.findAllContaining(page, yelloId).stream()
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

    public AdminQuestionResponse findQuestion(Long adminId, Pageable page) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        final Long totalCount = questionRepository.count();
        final List<AdminQuestionContentVO> list = questionRepository.findAll(page).stream()
            .map(AdminQuestionContentVO::of)
            .toList();

        return AdminQuestionResponse.of(totalCount, list);
    }

    public AdminQuestionDetailResponse findQuestionDetail(Long adminId, Long questionId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);
        final Question question = questionRepository.getById(questionId);

        // logic

        return AdminQuestionDetailResponse.of(question);
    }

    @Transactional
    public List<Vote> createVote(Long adminId, Long questionId, AdminQuestionVoteRequest request) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);
        final Question question = questionRepository.getById(questionId);

        // logic
        final List<Vote> result = new ArrayList<>();
        request.voteContentList().forEach((voteContent -> {
            final Optional<User> sender =
                userRepository.findByIdNotFiltered(voteContent.senderId());
            final Optional<User> receiver =
                userRepository.findByIdNotFiltered(voteContent.receiverId());

            if (sender.isPresent() && receiver.isPresent()) {
                final Vote vote = Vote.createVote(
                    voteContent.keyword(),
                    sender.get(),
                    receiver.get(),
                    question,
                    voteContent.colorIndex()
                );

                voteRepository.save(vote);
                result.add(vote);
            }
        }));

        return result;
    }

    @Transactional
    public void deleteQuestion(Long adminId, Long questionId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);
        final Question question = questionRepository.getById(questionId);

        // logic
        questionRepository.delete(question);
    }
}
