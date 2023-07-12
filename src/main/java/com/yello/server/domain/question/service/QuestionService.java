package com.yello.server.domain.question.service;

import com.yello.server.domain.question.entity.Question;

public interface QuestionService {
    Question findByQuestionId(Long questionId);
}
