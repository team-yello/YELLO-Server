package com.yello.server.domain.admin.service;

import static com.yello.server.domain.admin.entity.AdminConfigurationType.ADMIN_SITE_PASSWORD;
import static com.yello.server.global.common.ErrorCode.ADMIN_CONFIGURATION_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.DEVICE_TOKEN_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.PROBABILITY_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USER_ADMIN_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USER_ADMIN_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.admin.dto.request.AdminEventCreateRequest;
import com.yello.server.domain.admin.dto.request.AdminEventCreateRequest.EventRewardItemVO;
import com.yello.server.domain.admin.dto.request.AdminEventCreateRequest.EventRewardVO;
import com.yello.server.domain.admin.dto.request.AdminEventRewardCreateRequest;
import com.yello.server.domain.admin.dto.request.AdminLoginRequest;
import com.yello.server.domain.admin.dto.request.AdminNoticeCreateRequest;
import com.yello.server.domain.admin.dto.request.AdminQuestionVoteRequest;
import com.yello.server.domain.admin.dto.request.AdminUserDetailRequest;
import com.yello.server.domain.admin.dto.response.AdminConfigurationResponse;
import com.yello.server.domain.admin.dto.response.AdminCooldownContentVO;
import com.yello.server.domain.admin.dto.response.AdminCooldownResponse;
import com.yello.server.domain.admin.dto.response.AdminLoginResponse;
import com.yello.server.domain.admin.dto.response.AdminQuestionContentVO;
import com.yello.server.domain.admin.dto.response.AdminQuestionDetailResponse;
import com.yello.server.domain.admin.dto.response.AdminQuestionResponse;
import com.yello.server.domain.admin.dto.response.AdminUserContentVO;
import com.yello.server.domain.admin.dto.response.AdminUserDetailResponse;
import com.yello.server.domain.admin.dto.response.AdminUserResponse;
import com.yello.server.domain.admin.entity.AdminConfiguration;
import com.yello.server.domain.admin.entity.AdminConfigurationType;
import com.yello.server.domain.admin.exception.AdminConfigurationNotFoundException;
import com.yello.server.domain.admin.exception.UserAdminBadRequestException;
import com.yello.server.domain.admin.exception.UserAdminNotFoundException;
import com.yello.server.domain.admin.repository.AdminConfigurationRepository;
import com.yello.server.domain.admin.repository.AdminRepository;
import com.yello.server.domain.admin.repository.UserAdminRepository;
import com.yello.server.domain.authorization.service.AuthManager;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import com.yello.server.domain.notice.repository.NoticeRepository;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminConfigurationRepository adminConfigurationRepository;
    private final AdminRepository adminRepository;
    private final AuthManager authManager;
    private final CooldownRepository cooldownRepository;
    private final NoticeRepository noticeRepository;
    private final ObjectMapper objectMapper;
    private final QuestionRepository questionRepository;
    private final UserAdminRepository userAdminRepository;
    private final UserManager userManager;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public AdminLoginResponse login(AdminLoginRequest request) {
        final List<AdminConfiguration> list = adminConfigurationRepository.findConfigurations(
            ADMIN_SITE_PASSWORD);
        if (list.isEmpty()) {
            throw new AdminConfigurationNotFoundException(ADMIN_CONFIGURATION_NOT_FOUND_EXCEPTION);
        }

        String accessToken;
        String adminPassWord = list.get(0).getValue();

        if (request.password().equals(adminPassWord)) {
            final User user = userManager.getOfficialUser(Gender.FEMALE);

            accessToken = authManager.issueToken(user).accessToken();
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

        if (field == null || value == null) {
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

    public AdminConfigurationResponse getConfigurations(Long adminId, AdminConfigurationType tag) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        final List<AdminConfiguration> configurations = adminConfigurationRepository.findConfigurations(tag);

        if (configurations.isEmpty()) {
            throw new AdminConfigurationNotFoundException(ADMIN_CONFIGURATION_NOT_FOUND_EXCEPTION);
        }

        // logic
        return AdminConfigurationResponse.builder()
            .tag(String.valueOf(tag))
            .value(configurations.get(0).getValue())
            .build();
    }

    @Transactional
    public EmptyObject updateConfigurations(Long adminId, AdminConfigurationType tag, String value) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        adminConfigurationRepository.setConfigurations(tag, value);

        return EmptyObject.builder().build();
    }

    public List<Notice> getNotices(Long adminId) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        final List<Notice> noticeList = noticeRepository.findAll();

        return noticeList;
    }

    @Transactional
    public EmptyObject createNotice(Long adminId, AdminNoticeCreateRequest request) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        ZonedDateTime startDate = ZonedDateTime.parse(request.startDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        ZonedDateTime endDate = ZonedDateTime.parse(request.endDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        final NoticeType tag = NoticeType.fromCode(request.tag());

        final Notice notice = Notice.builder()
            .imageUrl(request.imageUrl())
            .redirectUrl(request.redirectUrl())
            .startDate(startDate)
            .endDate(endDate)
            .isAvailable(true)
            .tag(tag)
            .title(request.title())
            .build();

        noticeRepository.save(notice);

        return EmptyObject.builder().build();
    }

    @Transactional
    public EmptyObject updateNotice(Long adminId, Long noticeId, AdminNoticeCreateRequest request) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);
        final Notice notice = noticeRepository.getById(noticeId);

        ZonedDateTime startDate = ZonedDateTime.parse(request.startDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        ZonedDateTime endDate = ZonedDateTime.parse(request.endDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        final NoticeType tag = NoticeType.fromCode(request.tag());

        noticeRepository.update(Notice.builder()
            .id(notice.getId())
            .imageUrl(request.imageUrl())
            .redirectUrl(request.redirectUrl())
            .startDate(startDate)
            .endDate(endDate)
            .isAvailable(request.isAvailable())
            .tag(tag)
            .title(request.title())
            .build());

        return EmptyObject.builder().build();
    }

    @Transactional
    public EmptyObject createEvent(Long adminId, AdminEventCreateRequest request) throws JsonProcessingException {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        for (EventRewardVO vo : request.eventReward()) {
            int sumOfProbability = 0;

            for (EventRewardItemVO itemVO : vo.eventRewardItem()) {
                sumOfProbability += itemVO.eventRewardProbability();
            }

            if (sumOfProbability != 100) {
                throw new UserAdminBadRequestException(PROBABILITY_BAD_REQUEST_EXCEPTION);
            }
        }

        // logic
        String animationString = objectMapper.writeValueAsString(request.animationList());

        final Event newEvent = adminRepository.save(Event.builder()
            .tag(request.tag())
            .startDate(ZonedDateTime.parse(request.startDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            .endDate(ZonedDateTime.parse(request.endDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            .title(request.title())
            .subTitle(request.subTitle())
            .animation(animationString)
            .build());

        request.eventReward().forEach((eventRewardVO) -> {
            final EventTime newEventTime = adminRepository.save(EventTime.builder()
                .event(newEvent)
                .startTime(eventRewardVO.startTime())
                .endTime(eventRewardVO.endTime())
                .rewardCount(eventRewardVO.rewardCount())
                .build());

            eventRewardVO.eventRewardItem().forEach(eventRewardItemVO -> {
                final EventReward eventReward = adminRepository.getByTag(eventRewardItemVO.tag());

                adminRepository.save(EventRewardMapping.builder()
                    .eventTime(newEventTime)
                    .eventReward(eventReward)
                    .eventRewardProbability(eventRewardItemVO.eventRewardProbability())
                    .randomTag(eventRewardItemVO.randomTag())
                    .build());
            });
        });

        return EmptyObject.builder().build();
    }

    @Transactional
    public EmptyObject createEventReward(Long adminId, AdminEventRewardCreateRequest request) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // login
        adminRepository.save(EventReward.builder()
            .tag(request.tag())
            .maxRewardValue(request.maxRewardValue())
            .minRewardValue(request.minRewardValue())
            .title(request.title())
            .image(request.image())
            .build());

        return EmptyObject.builder().build();
    }
}
