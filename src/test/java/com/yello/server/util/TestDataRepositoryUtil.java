package com.yello.server.util;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionGroupType;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import java.time.LocalDateTime;

public class TestDataRepositoryUtil implements TestDataUtil {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;
    private final QuestionGroupTypeRepository questionGroupTypeRepository;

    public TestDataRepositoryUtil(UserRepository userRepository, VoteRepository voteRepository,
        QuestionRepository questionRepository,
        FriendRepository friendRepository, QuestionGroupTypeRepository questionGroupTypeRepository) {
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.questionRepository = questionRepository;
        this.friendRepository = friendRepository;
        this.questionGroupTypeRepository = questionGroupTypeRepository;
    }

    @Override
    public User generateUser(long index, long schoolIndex, UserGroupType userGroupType) {
        UserGroup userGroup = generateGroup(schoolIndex, userGroupType);

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
            .group(userGroup)
            .groupAdmissionYear(20)
            .deviceToken("deviceToken#%d".formatted(index))
            .subscribe(Subscribe.NORMAL)
            .ticketCount(0)
            .email("test%d@test.com".formatted(index))
            .build());
    }

    @Override
    public User generateDeletedUser(long index, long schoolIndex, UserGroupType userGroupType) {
        UserGroup userGroup = generateGroup(schoolIndex, userGroupType);

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
            .group(userGroup)
            .groupAdmissionYear(20)
            .deviceToken(null)
            .subscribe(Subscribe.NORMAL)
            .ticketCount(0)
            .email("test%d@test.com".formatted(index))
            .build());
    }

    @Override
    public Friend generateFriend(User user, User target) {
        return friendRepository.save(Friend.createFriend(user, target));
    }

    @Override
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

    @Override
    public Question generateQuestion(long index) {
        return questionRepository.save(
            Question.builder()
                .id(index)
                .nameHead("나는").nameFoot("와")
                .keywordHead("멋진").keywordFoot("에서 놀고싶어")
                .build()
        );
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
        return questionGroupTypeRepository.save(
            QuestionGroupType.builder()
                .id(index)
                .userGroupType(UserGroupType.UNIVERSITY)
                .question(question)
                .build()
        );
    }
}
