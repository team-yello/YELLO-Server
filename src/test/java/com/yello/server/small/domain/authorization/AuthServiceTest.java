package com.yello.server.small.domain.authorization;

import static com.yello.server.global.common.ErrorCode.GROUPID_NOT_FOUND_GROUP_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_REQUIRED_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriend;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.authorization.service.AuthService;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.group.repository.SchoolRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.factory.PaginationFactory;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import com.yello.server.small.domain.cooldown.FakeCooldownRepository;
import com.yello.server.small.domain.friend.FakeFriendRepository;
import com.yello.server.small.domain.group.FakeSchoolRepository;
import com.yello.server.small.domain.user.FakeUserRepository;
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
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(secretKey);
    private final TokenRepository tokenRepository = new FakeTokenRepository();
    private AuthService authService;

    @BeforeEach
    void init() {
        this.authService = AuthService.builder()
            .userRepository(userRepository)
            .schoolRepository(schoolRepository)
            .friendRepository(friendRepository)
            .cooldownRepository(cooldownRepository)
            .jwtTokenProvider(jwtTokenProvider)
            .tokenValueOperations(tokenRepository)
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
            .build());

        userRepository.save(User.builder()
            .id(1L)
            .recommendCount(0L).name("방형정")
            .yelloId("hj_p__").gender(Gender.FEMALE)
            .point(200).social(Social.KAKAO)
            .profileImage("NO_IMAGE").uuid("1234")
            .deletedAt(null).group(school)
            .groupAdmissionYear(23).email("hj_p__@yello.com")
            .build());

        userRepository.save(User.builder()
            .id(2L)
            .recommendCount(0L).name("궝셍훙")
            .yelloId("sh").gender(Gender.FEMALE)
            .point(200).social(Social.KAKAO)
            .profileImage("NO_IMAGE").uuid("12345")
            .deletedAt(null).group(school)
            .groupAdmissionYear(23).email("sh@yello.com")
            .build());

        userRepository.save(User.builder()
            .id(3L)
            .recommendCount(0L).name("잉응젱")
            .yelloId("ej").gender(Gender.FEMALE)
            .point(200).social(Social.KAKAO)
            .profileImage("NO_IMAGE").uuid("123456")
            .deletedAt(null).group(school)
            .groupAdmissionYear(23).email("ej@yello.com")
            .build());
    }

    @Test
    void 유저_삭제정보_초기화에_성공합니다() {
        // given
        Long userId = 0L;

        // when
        final User softDeletedUser = userRepository.getById(userId);
        authService.renewUserInformation(softDeletedUser);

        final List<LocalDateTime> targetIds =
            friendRepository.findAllByUserIdNotFiltered(softDeletedUser.getId())
                .stream()
                .map((friend) -> friend.getTarget().getDeletedAt())
                .toList();
        final List<LocalDateTime> userIds =
            friendRepository.findAllByTargetIdNotFiltered(softDeletedUser.getId())
                .stream()
                .map((friend) -> friend.getUser().getDeletedAt())
                .toList();
        final Optional<Cooldown> cooldown = cooldownRepository.findByUserId(userId);

        // then
        assertThat(softDeletedUser.getDeletedAt()).isNull();
        targetIds.forEach((deletedTime) -> assertThat(deletedTime).isNull());
        userIds.forEach((deletedTime) -> assertThat(deletedTime).isNull());
        cooldown.ifPresent((cooldown1) -> assertThat(cooldown1.getDeletedAt()).isNull());
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
        final User signUpUser = authService.signUpUser(request);
        final User expectedUser = userRepository.getById(signUpUser.getId());

        // then
        assertThat(signUpUser).isEqualTo(expectedUser);
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
        assertThatThrownBy(() -> authService.signUpUser(request))
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
        assertThatThrownBy(() -> authService.signUpUser(request))
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
        assertThatThrownBy(() -> authService.signUpUser(request))
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
    void 회원가입_토큰_등록에_성공합니다() {
        // given
        Long id = 1L;
        String uuid = "1234";

        // when
        final ServiceTokenVO token = authService.registerToken(id, uuid);
        final ServiceTokenVO registeredToken = tokenRepository.get(id);

        // then
//    assertThat(jwtTokenProvider.getUserId(token.accessToken())).isEqualTo(id);
//    assertThat(jwtTokenProvider.getUserId(token.refreshToken())).isEqualTo(id);
//    assertThat(jwtTokenProvider.getUserUuid(token.accessToken())).isEqualTo(uuid);
//    assertThat(jwtTokenProvider.getUserUuid(token.refreshToken())).isEqualTo(uuid);
        assertThat(registeredToken).isEqualTo(token);
    }

    @Test
    void 회원가입_친구_등록에_성공합니다() {
        // given
        Long id = 1L;
        List<Long> friendList = new ArrayList<>();
        friendList.add(2L);
        friendList.add(3L);

        // when
        final User user = userRepository.getById(id);
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