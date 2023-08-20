package com.yello.server.util;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import java.time.LocalDateTime;

public class TestDataUtil {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;

    public TestDataUtil(UserRepository userRepository, VoteRepository voteRepository,
        QuestionRepository questionRepository,
        FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.questionRepository = questionRepository;
        this.friendRepository = friendRepository;
    }

    public User generateUser(long index, long schoolIndex) {
        School school = generateSchool(schoolIndex);

        return userRepository.save(User.builder()
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
            .build());
    }

    public User generateDeletedUser(long index, long schoolIndex) {
        School school = generateSchool(schoolIndex);

        return userRepository.save(User.builder()
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
            .build());
    }

    public void generateFriend(User user, User target) {
        friendRepository.save(Friend.createFriend(user, target));
    }

    public Vote generateVote(long index, User sender, User receiver, Question question) {
        return voteRepository.save(
            Vote.builder()
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
                .build()
        );
    }

    public Question generateQuestion(long index) {
        return questionRepository.save(
            Question.builder()
                .id(index)
                .nameHead("나는").nameFoot("와")
                .keywordHead("멋진").keywordFoot("에서 놀고싶어")
                .build()
        );
    }

    public School generateSchool(long index) {
        return School.builder()
            .id(index)
            .schoolName("테스트 대학교 %d".formatted(index))
            .departmentName("테스트 학과 %d".formatted(index))
            .build();
    }

}
