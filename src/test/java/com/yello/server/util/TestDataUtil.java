package com.yello.server.util;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionGroupType;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.entity.Vote;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public interface TestDataUtil {

    User generateUser(long index, UserGroup userGroup);

    User generateDeletedUser(long index, UserGroup userGroup);

    Friend generateFriend(User user, User target);

    Vote generateVote(long index, User sender, User receiver, Question question);

    Question generateQuestion(long index);

    UserGroup generateGroup(long index, UserGroupType userGroupType);

    QuestionGroupType generateQuestionGroupType(long index, Question question);

    Purchase generatePurchase(long index, User user, LocalDateTime createdAt);

    Notice generateNotice(long index, NoticeType noticeType, ZonedDateTime createdAt);
}

