package com.yello.server.domain.question.service;

import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionRepository;
import com.yello.server.domain.question.exception.QuestionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_QUESTION_EXCEPTION;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public Question findByQuestionId(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(NOT_FOUND_QUESTION_EXCEPTION));
    }
}
