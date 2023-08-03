package com.yello.server.small.question;

import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.exception.QuestionNotFoundException;
import com.yello.server.domain.question.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_QUESTION_EXCEPTION;

public class FakeQuestionRepository implements QuestionRepository {

    private final List<Question> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public List<Question> findAll() {
        return data.stream()
                .toList();
    }

    @Override
    public Question findById(Long questionId) {
        return data.stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new QuestionNotFoundException(NOT_FOUND_QUESTION_EXCEPTION));
    }

    public void saveQuestion() {

    }
}
