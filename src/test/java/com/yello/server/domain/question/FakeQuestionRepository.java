package com.yello.server.domain.question;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_QUESTION_EXCEPTION;

import com.yello.server.domain.keyword.FakeKeywordRepository;
import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.keyword.repository.KeywordRepository;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.exception.QuestionNotFoundException;
import com.yello.server.domain.question.repository.QuestionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class FakeQuestionRepository implements QuestionRepository {

    private final KeywordRepository keywordRepository = new FakeKeywordRepository();
    private final List<Question> data = new ArrayList<>();
    private Long id = 0L;


    @Override
    public Long count() {
        return (long) data.size();
    }

    @Override
    public List<Question> findAll() {
        return data.stream()
            .toList();
    }

    @Override
    public Page<Question> findAll(Pageable pageable) {
        final List<Question> questionList = data.stream()
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
        return new PageImpl<>(questionList);
    }

    @Override
    public Question getById(Long id) {
        return data.stream()
            .filter(question -> question.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new QuestionNotFoundException(NOT_FOUND_QUESTION_EXCEPTION));
    }

    @Override
    public Optional<Question> findById(Long questionId) {
        return data.stream()
            .filter(question -> question.getId().equals(questionId))
            .findFirst();
    }

    @Override
    public Question save(Question question) {
        Question newQuestion = Question.builder()
            .id(question.getId() == null ? id++ : question.getId())
            .nameHead(question.getNameHead())
            .nameFoot(question.getNameFoot())
            .keywordFoot(question.getKeywordFoot())
            .keywordHead(question.getKeywordHead())
            .build();

        Keyword keyword1 = keywordRepository.save(Keyword.of("keyword", newQuestion));
        Keyword keyword2 = keywordRepository.save(Keyword.of("keyword", newQuestion));
        Keyword keyword3 = keywordRepository.save(Keyword.of("keyword", newQuestion));
        Keyword keyword4 = keywordRepository.save(Keyword.of("keyword", newQuestion));

        newQuestion.addKeyword(keyword1);
        newQuestion.addKeyword(keyword2);
        newQuestion.addKeyword(keyword3);
        newQuestion.addKeyword(keyword4);

        data.add(newQuestion);
        return newQuestion;
    }

    @Override
    public Optional<Question> findByQuestionContent(String nameHead, String nameFoot, String keywordHead,
        String keywordFoot) {
        return data.stream()
            .filter(question -> Objects.equals(question.getNameHead(), nameHead)
                && Objects.equals(question.getNameFoot(), nameFoot)
                && Objects.equals(question.getKeywordHead(), keywordHead)
                && Objects.equals(question.getKeywordFoot(), keywordFoot))
            .findFirst();
    }

    @Override
    public void delete(Question question) {
        data.remove(question);
    }
}
