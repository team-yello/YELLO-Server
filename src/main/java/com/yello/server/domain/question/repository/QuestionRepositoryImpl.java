package com.yello.server.domain.question.repository;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_QUESTION_EXCEPTION;

import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.exception.QuestionException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

    private final QuestionJpaRepository questionJpaRepository;

    @Override
    public List<Question> findAll() {
        return questionJpaRepository.findAll();
    }

    @Override
    public Question findById(Long id) {
        return questionJpaRepository.findById(id)
            .orElseThrow(() -> new QuestionException(NOT_FOUND_QUESTION_EXCEPTION));
    }

    @Override
    public Question save(Question question) {
        return questionJpaRepository.save(question);
    }

    @Override
    public Optional<Question> findByQuestionContent(String nameHead, String nameFoot, String keywordHead,
        String keywordFoot) {
        return questionJpaRepository.findByQuestionContent(nameHead, nameFoot, keywordHead, keywordFoot);
    }
}
