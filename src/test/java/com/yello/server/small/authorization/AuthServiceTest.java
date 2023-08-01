package com.yello.server.small.authorization;

import static com.yello.server.global.common.ErrorCode.YELLOID_REQUIRED_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.authorization.service.AuthService;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.repository.SchoolRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.small.cooldown.FakeCooldownRepository;
import com.yello.server.small.friend.FakeFriendRepository;
import com.yello.server.small.global.redis.FakeRedisValueOperation;
import com.yello.server.small.group.FakeSchoolRepository;
import com.yello.server.small.user.FakeUserRepository;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ValueOperations;

public class AuthServiceTest {

  private final String secretKey = Base64.getEncoder().encodeToString("keyForTest".getBytes());
  private final UserRepository userRepository = new FakeUserRepository();
  private final SchoolRepository schoolRepository = new FakeSchoolRepository();
  private final FriendRepository friendRepository = new FakeFriendRepository();
  private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
  private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(secretKey);
  private final ValueOperations<Long, ServiceTokenVO> tokenValueOperations = new FakeRedisValueOperation();
  private AuthService authService;

  @BeforeEach
  void init() {
    this.authService = AuthService.builder()
        .userRepository(userRepository)
        .schoolRepository(schoolRepository)
        .friendRepository(friendRepository)
        .cooldownRepository(cooldownRepository)
        .jwtTokenProvider(jwtTokenProvider)
        .tokenValueOperations(tokenValueOperations)
        .build();
    School school = School.builder()
        .id(1L)
        .schoolName("옐로대학교")
        .departmentName("국정원학과")
        .build();
    userRepository.save(User.builder()
        .id(1L)
        .recommendCount(0L).name("방형정")
        .yelloId("hj_p__").gender(Gender.FEMALE)
        .point(200).social(Social.KAKAO)
        .profileImage("NO_IMAGE").uuid("1234")
        .deletedAt(null).group(school)
        .groupAdmissionYear(23).email("hj_p__@yello.com")
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
  void 회원가입에_성공합니다() {

  }
}
