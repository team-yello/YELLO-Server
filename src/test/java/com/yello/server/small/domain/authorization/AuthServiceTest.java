package com.yello.server.small.domain.authorization;

import static com.yello.server.global.common.ErrorCode.GROUPID_NOT_FOUND_GROUP_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_REQUIRED_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriend;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;
import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.authorization.service.AuthManager;
import com.yello.server.domain.authorization.service.AuthService;
import com.yello.server.domain.authorization.service.TokenJwtProvider;
import com.yello.server.domain.authorization.service.TokenProvider;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.friend.service.FriendManager;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.group.repository.SchoolRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.domain.vote.service.VoteManager;
import com.yello.server.global.common.factory.PaginationFactory;
import com.yello.server.global.common.manager.ConnectionManager;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import com.yello.server.infrastructure.firebase.service.NotificationFcmService;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import com.yello.server.small.domain.cooldown.FakeCooldownRepository;
import com.yello.server.small.domain.friend.FakeFriendManager;
import com.yello.server.small.domain.friend.FakeFriendRepository;
import com.yello.server.small.domain.group.FakeSchoolRepository;
import com.yello.server.small.domain.question.FakeQuestionRepository;
import com.yello.server.small.domain.user.FakeUserManager;
import com.yello.server.small.domain.user.FakeUserRepository;
import com.yello.server.small.domain.vote.FakeVoteManager;
import com.yello.server.small.domain.vote.FakeVoteRepository;
import com.yello.server.small.global.firebase.FakeFcmManger;
import com.yello.server.small.global.redis.FakeTokenRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

public class AuthServiceTest {

    private final String secretKey = Base64.getEncoder().encodeToString(
        "keyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTest".getBytes());

    private final UserRepository userRepository = new FakeUserRepository();
    private final SchoolRepository schoolRepository = new FakeSchoolRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
    private final TokenRepository tokenRepository = new FakeTokenRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();

    private final TokenProvider tokenProvider = new TokenJwtProvider(secretKey);
    private final AuthManager authManager = new FakeAuthManager(
        friendRepository, cooldownRepository, userRepository, tokenRepository, tokenProvider
    );
    private final UserManager userManager = new FakeUserManager(userRepository);
    private final VoteManager voteManager = new FakeVoteManager(
        userRepository, questionRepository, voteRepository, friendRepository,
        userManager);
    private final FriendManager friendManager = new FakeFriendManager(userRepository);
    private final ConnectionManager connectionManager = new FakeConnectionManager();
    private final FCMManager fcmManager = new FakeFcmManger();

    private final NotificationService notificationService = NotificationFcmService.builder()
        .userRepository(userRepository)
        .tokenRepository(tokenRepository)
        .fcmManager(fcmManager)
        .build();
    private AuthService authService;

    @BeforeEach
    void init() {
        this.authService = AuthService.builder()
            .userRepository(userRepository)
            .schoolRepository(schoolRepository)
            .friendRepository(friendRepository)
            .cooldownRepository(cooldownRepository)
            .authManager(authManager)
            .friendManager(friendManager)
            .connectionManager(connectionManager)
            .voteManager(voteManager)
            .tokenProvider(tokenProvider)
            .notificationService(notificationService)
            .build();

        School school = School.builder()
            .id(1L)
            .schoolName("옐로대학교")
            .departmentName("국정원학과")
            .build();

        schoolRepository.save(school);

        // soft-deleted User
        userRepository.save(User.builder()
            .id(0L)
            .recommendCount(0L).name("잉강밍")
            .yelloId("gm").gender(Gender.FEMALE)
            .point(200).social(Social.KAKAO)
            .profileImage("NO_IMAGE").uuid("123")
            .deletedAt(LocalDateTime.now()).group(school)
            .groupAdmissionYear(23).email("gm@yello.com")
            .deviceToken("12345")
            .build());

        userRepository.save(User.builder()
            .id(1L)
            .recommendCount(0L).name("방형정")
            .yelloId("hj_p__").gender(Gender.FEMALE)
            .point(200).social(Social.KAKAO)
            .profileImage("NO_IMAGE").uuid("1234")
            .deletedAt(null).group(school)
            .groupAdmissionYear(23).email("hj_p__@yello.com")
            .deviceToken("12345")
            .build());

        userRepository.save(User.builder()
            .id(2L)
            .recommendCount(0L).name("궝셍훙")
            .yelloId("sh").gender(Gender.FEMALE)
            .point(200).social(Social.KAKAO)
            .profileImage("NO_IMAGE").uuid("12345")
            .deletedAt(null).group(school)
            .groupAdmissionYear(23).email("sh@yello.com")
            .deviceToken("12345")
            .build());

        userRepository.save(User.builder()
            .id(3L)
            .recommendCount(0L).name("잉응젱")
            .yelloId("ej").gender(Gender.FEMALE)
            .point(200).social(Social.KAKAO)
            .profileImage("NO_IMAGE").uuid("123456")
            .deletedAt(null).group(school)
            .groupAdmissionYear(23).email("ej@yello.com")
            .deviceToken("12345")
            .build());
    }

    @Test
    void Yello_Id_중복_조회에_성공합니다_중복임() {
        // given
        String yelloId = "hj_p__";

        // when
        Boolean isDuplicated = authService.isYelloIdDuplicated(yelloId);

        // then
        assertThat(isDuplicated).isEqualTo(true);
    }

    @Test
    void Yello_Id_중복_조회에_성공합니다_중복아님() {
        // given
        String yelloId = "hj_p__123123";

        // when
        Boolean isDuplicated = authService.isYelloIdDuplicated(yelloId);

        // then
        assertThat(isDuplicated).isEqualTo(false);
    }

    @Test
    void Yello_Id가_NULL일시_AuthBadRequestException이_발생합니다() {
        // given

        // when

        // then
        assertThatThrownBy(() -> authService.isYelloIdDuplicated(null))
            .isInstanceOf(AuthBadRequestException.class)
            .hasMessageContaining(YELLOID_REQUIRED_EXCEPTION.getMessage());
    }

    @Test
    void 회원가입_신규_유저_등록에_성공합니다() {
        // given
        final SignUpRequest request = SignUpRequest.builder()
            .social(Social.KAKAO)
            .uuid("123456789")
            .email("agenda511@kakao.com")
            .profileImage("NO_IMAGE")
            .groupId(1L)
            .groupAdmissionYear(19)
            .name("이의제")
            .yelloId("agenda511")
            .gender(Gender.MALE)
            .friends(new ArrayList<>())
            .recommendId("")
            .build();

        // when
        SignUpResponse signUpUser = authService.signUp(request);
        final User expectedUser = userRepository.getByYelloId(signUpUser.yelloId());

        // then
        assertThat(signUpUser.yelloId()).isEqualTo(expectedUser.getYelloId());
    }

    @Test
    void 회원가입_신규_유저_등록에_실패합니다_uuid_충돌() {
        // given
        final SignUpRequest request = SignUpRequest.builder()
            .social(Social.KAKAO)
            .uuid("1234")
            .email("agenda511@kakao.com")
            .profileImage("NO_IMAGE")
            .groupId(1L)
            .groupAdmissionYear(19)
            .name("이의제")
            .yelloId("agenda511")
            .gender(Gender.MALE)
            .friends(new ArrayList<>())
            .recommendId("")
            .build();

        // when

        // then
        assertThatThrownBy(() -> authService.signUp(request))
            .isInstanceOf(UserConflictException.class)
            .hasMessageContaining(UUID_CONFLICT_USER_EXCEPTION.getMessage());
    }

    @Test
    void 회원가입_신규_유저_등록에_실패합니다_yelloId_충돌() {
        // given
        final SignUpRequest request = SignUpRequest.builder()
            .social(Social.KAKAO)
            .uuid("12345678")
            .email("agenda511@kakao.com")
            .profileImage("NO_IMAGE")
            .groupId(1L)
            .groupAdmissionYear(19)
            .name("이의제")
            .yelloId("hj_p__")
            .gender(Gender.MALE)
            .friends(new ArrayList<>())
            .recommendId("")
            .build();

        // when

        // then
        assertThatThrownBy(() -> authService.signUp(request))
            .isInstanceOf(UserConflictException.class)
            .hasMessageContaining(YELLOID_CONFLICT_USER_EXCEPTION.getMessage());
    }

    @Test
    void 회원가입_신규_유저_등록에_실패합니다_존재하지_않는_group() {
        // given
        final SignUpRequest request = SignUpRequest.builder()
            .social(Social.KAKAO)
            .uuid("12345678")
            .email("agenda511@kakao.com")
            .profileImage("NO_IMAGE")
            .groupId(2L)
            .groupAdmissionYear(19)
            .name("이의제")
            .yelloId("_euije")
            .gender(Gender.MALE)
            .friends(new ArrayList<>())
            .recommendId("")
            .build();

        // when

        // then
        assertThatThrownBy(() -> authService.signUp(request))
            .isInstanceOf(GroupNotFoundException.class)
            .hasMessageContaining(GROUPID_NOT_FOUND_GROUP_EXCEPTION.getMessage());
    }

    @Test
    void 회원가입_친구_추천에_성공합니다_추천수() {
        // given
        String recommendYelloId = "hj_p__";

        // when
        final Long before = userRepository.getByYelloId(recommendYelloId).getRecommendCount();
        authService.recommendUser(recommendYelloId);
        final User after = userRepository.getByYelloId(recommendYelloId);

        // then
        assertThat(after.getRecommendCount()).isEqualTo(before + 1L);
    }

    @Test
    void 회원가입_친구_추천에_성공합니다_쿨다운삭제() {
        // given
        String recommendYelloId = "hj_p__";
        final User before = userRepository.getByYelloId(recommendYelloId);
        cooldownRepository.save(Cooldown.builder()
            .user(before)
            .build());

        // when
        authService.recommendUser(recommendYelloId);
        final User after = userRepository.getByYelloId(recommendYelloId);
        final Optional<Cooldown> cooldown = cooldownRepository.findByUserId(after.getId());

        // then
        assertThat(cooldown.isEmpty()).isEqualTo(true);
    }

    @Test
    void 회원가입_친구_등록에_성공합니다() {
        // given
        final Long id = 1L;
        final User user = userRepository.getById(id);
        List<Long> friendList = new ArrayList<>();
        friendList.add(2L);
        friendList.add(3L);

        // when

        authService.makeFriend(user, friendList);

        final List<Long> targetId = friendRepository.findAllByUserId(user.getId())
            .stream()
            .map((friend) -> friend.getTarget().getId())
            .toList();
        final List<Long> userId = friendRepository.findAllByTargetId(user.getId())
            .stream()
            .map((friend) -> friend.getUser().getId())
            .toList();

        // then
        assertThat(friendList).isSubsetOf(targetId);
        assertThat(friendList).isSubsetOf(userId);
    }

    @Test
    void 회원가입_친구_등록에_실패합니다() {
        // given

        /**
         * NOTE - 실패 케이스가 없습니다.
         */

        // when

        // then
    }

    @Test
    void 회원가입_추천친구목록_조회에_성공합니다() {
        // given
        List<String> kakaoFriendList = new ArrayList<>();
        kakaoFriendList.add("123"); // soft-deleted user
        kakaoFriendList.add("1234");
        kakaoFriendList.add("12345");
        kakaoFriendList.add("123456");

        final List<Long> expectedList = Stream.of("123", "1234", "12345", "123456")
            .map(userRepository::getByUuid)
            .map(User::getId)
            .toList();

        final OnBoardingFriendRequest request = OnBoardingFriendRequest.builder()
            .friendKakaoId(kakaoFriendList)
            .build();
        final Pageable pageable = PaginationFactory.createPageable(0);

        // when
        final OnBoardingFriendResponse response =
            authService.findOnBoardingFriends(request, pageable);

        // then
        assertThat(response.totalCount() - 1).isEqualTo(3);
        assertThat(expectedList.stream()
            .sorted()
            .toList())
            .isEqualTo(response.friendList()
                .stream()
                .map(OnBoardingFriend::id)
                .sorted()
                .toList());
    }
}
