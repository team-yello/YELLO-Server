package com.yello.server.small.friend;

import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.FriendsResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.exception.FriendNotFoundException;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.friend.service.FriendService;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.small.user.FakeUserRepository;
import com.yello.server.small.vote.FakeVoteRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class FriendServiceTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private FriendService friendService;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;

    @BeforeEach
    void init() {
        this.friendService = FriendService.builder()
            .userRepository(userRepository)
            .friendRepository(friendRepository)
            .voteRepository(voteRepository)
            .build();
        School school = School.builder()
            .id(1L)
            .schoolName("Test School")
            .departmentName("Testing")
            .build();
        user1 = userRepository.save(User.builder()
            .id(1L)
            .recommendCount(0L).name("test")
            .yelloId("yelloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image").uuid("1234")
            .deletedAt(null).group(school)
            .groupAdmissionYear(20).email("test@test.com")
            .build());
        user2 = userRepository.save(User.builder()
            .id(2L)
            .recommendCount(0L).name("hello")
            .yelloId("helloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image 2").uuid("5678")
            .deletedAt(null).group(school)
            .groupAdmissionYear(17).email("hello@test.com")
            .build());
        user3 = userRepository.save(User.builder()
            .id(3L)
            .recommendCount(0L).name("yello")
            .yelloId("yelloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image 3").uuid("91011")
            .deletedAt(null).group(school)
            .groupAdmissionYear(19).email("yello@test.com")
            .build());
        user4 = userRepository.save(User.builder()
            .id(4L)
            .recommendCount(0L).name("aaa")
            .yelloId("aaa").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image 4").uuid("aaa")
            .deletedAt(null).group(school)
            .groupAdmissionYear(19).email("aaa@test.com")
            .build());
        user5 = userRepository.save(User.builder()
            .id(5L)
            .recommendCount(0L).name("bbb")
            .yelloId("aaa").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image 5").uuid("bbb")
            .deletedAt(null).group(school)
            .groupAdmissionYear(19).email("bbb@test.com")
            .build());
        friendRepository.save(Friend.createFriend(user1, user2));
        friendRepository.save(Friend.createFriend(user2, user1));
    }

    @Test
    void 친구_전체_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final Integer page = 0;
        final Pageable pageable = createPageable(page);

        // when
        final FriendsResponse friends = friendService.findAllFriends(pageable, userId);

        // then
        assertThat(friends.totalCount()).isEqualTo(1);
        assertThat(friends.friends().get(0).name()).isEqualTo("hello");
    }

    @Test
    void 친구_추가에_성공합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 3L;

        // when
        friendService.addFriend(userId, targetId);
        final Friend friend = friendRepository.findByUserAndTarget(userId, targetId);

        // then
        assertThat(friend.getUser().getName()).isEqualTo("test");
        assertThat(friend.getTarget().getName()).isEqualTo("yello");
    }

    @Test
    void 친구_추가_시_존재하지_않는_유저_id인_경우에_UserNotFoundException이_발생합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 999L;

        // when
        // then
        assertThatThrownBy(() -> friendService.addFriend(userId, targetId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 친구_추가_시_이미_친구인_경우에_FriendException이_발생합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 2L;

        // when
        // then
        assertThatThrownBy(() -> friendService.addFriend(userId, targetId))
            .isInstanceOf(FriendException.class)
            .hasMessageContaining("[FriendException] 이미 존재하는 친구입니다.");
    }

    @Test
    void 친구_셔플에_성공합니다() {
        // given
        final Long userId = 1L;

        // when
        friendRepository.save(Friend.createFriend(user1, user3));
        friendRepository.save(Friend.createFriend(user1, user4));
        friendRepository.save(Friend.createFriend(user1, user5));

        List<FriendShuffleResponse> firstShuffledList = friendService.findShuffledFriend(userId);
        List<FriendShuffleResponse> secoundShuffledList = friendService.findShuffledFriend(userId);

        // then
        assertThat(firstShuffledList.size()).isEqualTo(4);
        assertThat(secoundShuffledList.size()).isEqualTo(4);
        assertThat(secoundShuffledList).isNotEqualTo(firstShuffledList);
    }

    @Test
    void 친구_셔플_시_존재하지_않는_유저_id인_경우에_UserNotFoundException이_발생합니다() {
        // given
        final Long userId = 999L;

        // when
        // then
        assertThatThrownBy(() -> friendService.findShuffledFriend(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 친구_셔플_시_친구_수가_부족한_경우에_FriendException이_발생합니다() {
        // given
        final Long userId = 1L;

        // when
        // then
        assertThatThrownBy(() -> friendService.findShuffledFriend(userId))
            .isInstanceOf(FriendException.class)
            .hasMessageContaining("[FriendException] 친구가 4명 이하입니다.");
    }

    @Test
    void 학교_추천_친구_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final Integer page = 0;
        final Pageable pageable = createPageable(page);

        // when
        final RecommendFriendResponse recommendSchoolFriends = friendService.findAllRecommendSchoolFriends(
            pageable,
            userId
        );

        // then
        assertThat(recommendSchoolFriends.totalCount()).isEqualTo(3);
    }

    @Test
    void 친구_끊기에_성공합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 2L;

        // when
        friendService.deleteFriend(userId, targetId);
        final List<Friend> friends = friendRepository.findAllByUserIdNotFiltered(userId);

        // then
        assertThat(friends.get(0).getDeletedAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_유저_친구_끊기_시_UserNotFoundException이_발생합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 999L;

        // when
        // then
        assertThatThrownBy(() -> friendService.deleteFriend(userId, targetId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 친구_끊기_시_친구가_아니라면_FriendNotFoundException이_발생합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 4L;

        // when
        // then
        assertThatThrownBy(() -> friendService.deleteFriend(userId, targetId))
            .isInstanceOf(FriendNotFoundException.class)
            .hasMessageContaining("[FriendNotFoundException] 존재하지 않는 친구이거나 친구 관계가 아닙니다.");
    }

    @Test
    void 카카오_추천_친구_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final Integer page = 0;
        final Pageable pageable = createPageable(page);
        final String[] friendKakaoId = {"5678", "91011", "aaa", "bbb"};
        final KakaoRecommendRequest request = KakaoRecommendRequest.builder()
            .friendKakaoId(friendKakaoId)
            .build();

        // when
        final RecommendFriendResponse allRecommendKakaoFriends = friendService.findAllRecommendKakaoFriends(
            pageable,
            userId,
            request
        );

        // then
        assertThat(allRecommendKakaoFriends.totalCount()).isEqualTo(3);
    }
}