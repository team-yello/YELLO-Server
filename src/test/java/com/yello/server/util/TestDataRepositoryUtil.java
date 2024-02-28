package com.yello.server.util;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.group.repository.UserGroupRepository;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import com.yello.server.domain.notice.repository.NoticeRepository;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionGroupType;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserDataRepository;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class TestDataRepositoryUtil implements TestDataUtil {

    private final FriendRepository friendRepository;
    private final NoticeRepository noticeRepository;
    private final PurchaseRepository purchaseRepository;
    private final QuestionGroupTypeRepository questionGroupTypeRepository;
    private final QuestionRepository questionRepository;
    private final TestDataEntityUtil testDataEntityUtil;
    private final UserDataRepository userDataRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public TestDataRepositoryUtil(FriendRepository friendRepository, NoticeRepository noticeRepository,
        PurchaseRepository purchaseRepository, QuestionGroupTypeRepository questionGroupTypeRepository,
        QuestionRepository questionRepository, TestDataEntityUtil testDataEntityUtil,
        UserDataRepository userDataRepository,
        UserGroupRepository userGroupRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.friendRepository = friendRepository;
        this.noticeRepository = noticeRepository;
        this.purchaseRepository = purchaseRepository;
        this.questionGroupTypeRepository = questionGroupTypeRepository;
        this.questionRepository = questionRepository;
        this.testDataEntityUtil = testDataEntityUtil;
        this.userDataRepository = userDataRepository;
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public User generateUser(long index, UserGroup userGroup) {
        return userRepository.save(testDataEntityUtil.generateUser(index, userGroup));
    }

    @Override
    public User generateDeletedUser(long index, UserGroup userGroup) {
        return userRepository.save(testDataEntityUtil.generateDeletedUser(index, userGroup));
    }

    @Override
    public Friend generateFriend(User user, User target) {
        return friendRepository.save(testDataEntityUtil.generateFriend(user, target));
    }

    @Override
    public Vote generateVote(long index, User sender, User receiver, Question question) {
        return voteRepository.save(testDataEntityUtil.generateVote(index, sender, receiver, question));
    }

    @Override
    public Question generateQuestion(long index) {
        return questionRepository.save(testDataEntityUtil.generateQuestion(index));
    }

    @Override
    public UserGroup generateGroup(long index, UserGroupType userGroupType) {
        return userGroupRepository.save(testDataEntityUtil.generateGroup(index, userGroupType));
    }

    @Override
    public QuestionGroupType generateQuestionGroupType(long index, Question question) {
        return questionGroupTypeRepository.save(testDataEntityUtil.generateQuestionGroupType(index, question));
    }

    @Override
    public Purchase generatePurchase(long index, User user, LocalDateTime createdAt) {
        return purchaseRepository.save(testDataEntityUtil.generatePurchase(index, user, createdAt));
    }

    @Override
    public Notice generateNotice(long index, NoticeType noticeType, ZonedDateTime createdAt) {
        return noticeRepository.save(testDataEntityUtil.generateNotice(index, noticeType, createdAt));
    }
}
