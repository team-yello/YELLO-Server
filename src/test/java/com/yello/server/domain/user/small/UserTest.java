package com.yello.server.domain.user.small;

import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.util.TestDataEntityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("User 엔티티에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class UserTest {

    private final TestDataEntityUtil testDataEntityUtil = new TestDataEntityUtil();

    private User user;
    private User deletedUser;

    @BeforeEach
    void init() {
        user = testDataEntityUtil.generateUser(1L, 1L);
        deletedUser = testDataEntityUtil.generateDeletedUser(2L, 1L);
    }

    @Test
    void 삭제에_성공합니다() {
        // given
        assertThat(user.getDeletedAt()).isNull();

        // when
        user.delete();

        // then
        assertThat(user.getDeletedAt()).isNotNull();
        assertThat(user.getPoint()).isZero();
        assertThat(user.getDeviceToken()).isNull();
    }

    @Test
    void 재가입에_성공합니다() {
        // given
        assertThat(deletedUser.getDeletedAt()).isNotNull();
        assertThat(deletedUser.getPoint()).isZero();
        assertThat(deletedUser.getDeviceToken()).isNull();

        // when
        deletedUser.renew();

        // then
        assertThat(deletedUser.getDeletedAt()).isNull();
        assertThat(deletedUser.getPoint()).isZero();
    }

    @Test
    void 그룹명_가져오기에_성공합니다() {
        // given
        // when
        // then
        assertThat(user.groupString()).isEqualTo("테스트 대학교 1 테스트 학과 1 20학번");
    }

    @Test
    void 추천_카운트_증가에_성공합니다() {
        // given
        assertThat(user.getRecommendCount()).isZero();

        // when
        user.increaseRecommendCount();

        // then
        assertThat(user.getRecommendCount()).isEqualTo(1);
    }

    @Test
    void 포인트_증가에_성공합니다() {
        // given
        assertThat(user.getPoint()).isEqualTo(200);

        // when
        user.increaseRecommendPoint();

        // then
        assertThat(user.getPoint()).isEqualTo(300);
    }

    @Test
    void 포인트_감소에_성공합니다() {
        // given
        assertThat(user.getPoint()).isEqualTo(200);

        // when
        user.minusPoint(100);

        // then
        assertThat(user.getPoint()).isEqualTo(100);
    }

    @Test
    void 디바이스_토큰_조회에_성공합니다() {
        // given
        // when
        // then
        assertThat(user.getDeviceToken()).isEqualTo("deviceToken#1");
    }

    @Test
    void 디바이스_토큰_수정에_성공합니다() {
        // given
        assertThat(user.getDeviceToken()).isEqualTo("deviceToken#1");

        // when
        user.setDeviceToken("newDeviceToken");

        // then
        assertThat(user.getDeviceToken()).isEqualTo("newDeviceToken");
    }

    @Test
    void 구독_여부_수정에_성공합니다() {
        // given
        assertThat(user.getSubscribe()).isEqualTo(Subscribe.NORMAL);

        // when
        user.setSubscribe(Subscribe.ACTIVE);

        // then
        assertThat(user.getSubscribe()).isEqualTo(Subscribe.ACTIVE);
    }

    @Test
    void 티켓_수량_수정에_성공합니다() {
        // given
        assertThat(user.getTicketCount()).isZero();

        // when
        user.addTicketCount(10);

        // then
        assertThat(user.getTicketCount()).isEqualTo(10);
    }

}
