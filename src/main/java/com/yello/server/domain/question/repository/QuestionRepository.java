package com.yello.server.domain.question.repository;

import com.yello.server.domain.question.entity.Question;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepository {

    Long count();

    List<Question> findAll();

    Page<Question> findAll(Pageable page);

    Question getById(Long id);

    Optional<Question> findById(Long id);

    Question save(Question question);

    Optional<Question> findByQuestionContent(String nameHead, String nameFoot, String keywordHead, String keywordFoot);

    void delete(Question question);
}
