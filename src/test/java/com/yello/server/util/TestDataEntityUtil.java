package com.yello.server.util;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.entity.Vote;
import java.time.LocalDateTime;

public class TestDataEntityUtil implements TestDataUtil {

    @Override
    public User generateUser(long index, long schoolIndex) {
        School school = School.builder()
            .id(schoolIndex)
            .schoolName("테스트 대학교 %d".formatted(index))
            .departmentName("테스트 학과 %d".formatted(index))
            .build();

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
            .group(school)
            .groupAdmissionYear(20)
            .deviceToken("deviceToken#%d".formatted(index))
            .subscribe(Subscribe.NORMAL)
            .ticketCount(0)
            .email("test%d@test.com".formatted(index))
            .build();
    }

    @Override
    public User generateDeletedUser(long index, long schoolIndex) {
        School school = School.builder()
            .id(schoolIndex)
            .schoolName("테스트 대학교 %d".formatted(index))
            .departmentName("테스트 학과 %d".formatted(index))
            .build();

        return User.builder()
            .id(index)
            .recommendCount(0L)
            .name("name%d".formatted(index))
            .yelloId("yelloId%d".formatted(index))
            .gender(Gender.MALE)
            .point(0)
            .social(Social.KAKAO)
            .profileImage("test image")
            .uuid("%d".formatted(index))
            .deletedAt(LocalDateTime.now())
            .group(school)
            .groupAdmissionYear(20)
            .deviceToken(null)
            .subscribe(Subscribe.NORMAL)
            .ticketCount(0)
            .email("test%d@test.com".formatted(index))
            .build();
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
    public School generateSchool(long index) {
        return School.builder()
            .id(index)
            .schoolName("테스트 대학교 %d".formatted(index))
            .departmentName("테스트 학과 %d".formatted(index))
            .build();
    }
}