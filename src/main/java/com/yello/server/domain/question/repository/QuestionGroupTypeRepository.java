package com.yello.server.domain.question.repository;

import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionGroupType;
import java.util.List;

public interface QuestionGroupTypeRepository {

    List<Question> findQuestionByGroupType(UserGroupType userGroupType);

    QuestionGroupType save(QuestionGroupType questionGroupType);
}
