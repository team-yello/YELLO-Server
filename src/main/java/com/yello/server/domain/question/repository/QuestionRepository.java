package com.yello.server.domain.question.repository;

import com.yello.server.domain.question.entity.Question;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository {

    List<Question> findAll();

    Question findById(Long id);

    Question save(Question question);

    Optional<Question> findByQuestionContent(String nameHead, String nameFoot, String keywordHead, String keywordFoot);
}
