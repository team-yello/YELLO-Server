package com.yello.server.domain.question.repository;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_QUESTION_EXCEPTION;

import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.exception.QuestionException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

    private final QuestionJpaRepository questionJpaRepository;

    @Override
    public Long count() {
        return questionJpaRepository.count();
    }

    @Override
    public List<Question> findAll() {
        return questionJpaRepository.findAll();
    }

    @Override
    public Page<Question> findAll(Pageable page) {
        final List<Question> questionList = questionJpaRepository.findAll(page)
            .stream()
            .toList();
        return new PageImpl<>(questionList);
    }

    @Override
    public Question getById(Long id) {
        return questionJpaRepository.findById(id)
            .orElseThrow(() -> new QuestionException(NOT_FOUND_QUESTION_EXCEPTION));
    }

    @Override
    public Optional<Question> findById(Long id) {
        return questionJpaRepository.findById(id);
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

    @Override
    public void delete(Question question) {
        questionJpaRepository.delete(question);
    }
}
