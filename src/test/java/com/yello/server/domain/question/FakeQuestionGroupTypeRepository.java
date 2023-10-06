package com.yello.server.domain.question;

import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionGroupType;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import java.util.ArrayList;
import java.util.List;

public class FakeQuestionGroupTypeRepository implements QuestionGroupTypeRepository {
    private final List<QuestionGroupType> data = new ArrayList<>();
    private Long id = 0L;
    private final QuestionRepository questionRepository;

    public FakeQuestionGroupTypeRepository(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public List<Question> findQuestionByGroupType(UserGroupType userGroupType) {
        List<Question> userGroupQuestion = new ArrayList<>();

        for (QuestionGroupType questionGroupType : data) {
            if (questionGroupType.getUserGroupType().equals(userGroupType)) {
                Long questionId = questionGroupType.getQuestion().getId();
                questionRepository.findById(questionId).ifPresent(it -> userGroupQuestion.add(it));
            }
        }
        return userGroupQuestion;
    }

    @Override
    public QuestionGroupType save(QuestionGroupType questionGroupType) {
        QuestionGroupType newQuestionGroupType = QuestionGroupType.builder()
            .id(questionGroupType.getId()==null ? id++ : questionGroupType.getId())
            .userGroupType(UserGroupType.UNIVERSITY)
            .question(questionGroupType.getQuestion())
            .build();

        data.add(newQuestionGroupType);
        return newQuestionGroupType;
    }
}
