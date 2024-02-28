package com.yello.server.util;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.entity.PurchaseState;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionGroupType;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.entity.Vote;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class TestDataEntityUtil implements TestDataUtil {

    @Override
    public User generateUser(long index, UserGroup userGroup) {
        return User.builder()
            .id((index))
            .recommendCount(0L)
            .name("name%d".formatted(index))
            .yelloId("yelloId%d".formatted(index))
            .gender(Gender.MALE)
            .point(200)
            .social(Social.KAKAO)
            .profileImage("test image")
            .uuid("%d".formatted(index))
            .deletedAt(null)
            .group(userGroup)
            .groupAdmissionYear(20)
            .deviceToken("deviceToken#%d".formatted(index))
            .subscribe(Subscribe.NORMAL)
            .ticketCount(0)
            .email("test%d@test.com".formatted(index))
            .build();
    }

    @Override
    public User generateDeletedUser(long index, UserGroup userGroup) {
        final User user = generateUser(index, userGroup);
        user.delete();

        return user;
    }

    @Override
    public Friend generateFriend(User user, User target) {
        return Friend.createFriend(user, target);
    }

    @Override
    public Vote generateVote(long index, User sender, User receiver, Question question) {
        return Vote.builder()
            .id(index)
            .colorIndex(0)
            .answer("test")
            .isRead(false)
            .nameHint(-1)
            .isAnswerRevealed(false)
            .sender(sender)
            .receiver(receiver)
            .question(question)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Override
    public Question generateQuestion(long index) {
        return Question.builder()
            .id(index)
            .nameHead("나는").nameFoot("와")
            .keywordHead("멋진").keywordFoot("에서 놀고싶어")
            .build();
    }

    @Override
    public UserGroup generateGroup(long index, UserGroupType userGroupType) {
        return UserGroup.builder()
            .id(index)
            .groupName("테스트 그룹 %d".formatted(index))
            .subGroupName("테스트 하위 그룹 %d".formatted(index))
            .userGroupType(userGroupType)
            .build();
    }

    @Override
    public QuestionGroupType generateQuestionGroupType(long index, Question question) {
        return QuestionGroupType.builder()
            .userGroupType(UserGroupType.UNIVERSITY)
            .question(question)
            .build();
    }

    @Override
    public Purchase generatePurchase(long index, User user, LocalDateTime createdAt) {
        return Purchase.builder()
            .id(index)
            .gateway(Gateway.APPLE)
            .price(1000)
            .productType(ProductType.YELLO_PLUS)
            .user(user)
            .transactionId("111")
            .state(PurchaseState.ACTIVE)
            .createdAt(createdAt)
            .updatedAt(createdAt)
            .build();
    }

    @Override
    public Notice generateNotice(long index, NoticeType noticeType, ZonedDateTime createdAt) {
        return Notice.builder()
            .id(index)
            .endDate(createdAt.plusDays(3))
            .imageUrl("imageUrl")
            .startDate(createdAt.minusDays(3))
            .redirectUrl("redirectUrl")
            .isAvailable(true)
            .tag(noticeType)
            .title("notice title")
            .build();
    }
}
