package com.yello.server.domain.question.repository;

import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionGroupType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionGroupTypeRepositoryImpl implements QuestionGroupTypeRepository {

    private final QuestionGroupTypeJpaRepository questionGroupTypeJpaRepository;

    @Override
    public List<Question> findQuestionByGroupType(UserGroupType userGroupType) {
        return questionGroupTypeJpaRepository.findQuestionByGroupType(userGroupType);
    }

    @Override
    public QuestionGroupType save(QuestionGroupType questionGroupType) {
        return questionGroupTypeJpaRepository.save(questionGroupType);
    }
}
