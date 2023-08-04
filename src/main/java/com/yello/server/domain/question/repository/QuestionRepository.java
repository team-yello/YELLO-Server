package com.yello.server.domain.question.repository;

import com.yello.server.domain.question.entity.Question;
import java.util.List;

public interface QuestionRepository {

    List<Question> findAll();

    Question findById(Long id);
}
