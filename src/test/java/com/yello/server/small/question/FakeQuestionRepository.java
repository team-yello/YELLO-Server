package com.yello.server.small.question;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_QUESTION_EXCEPTION;

import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.keyword.repository.KeywordRepository;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.exception.QuestionNotFoundException;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.small.keyword.FakeKeywordRepository;
import java.util.ArrayList;
import java.util.List;

public class FakeQuestionRepository implements QuestionRepository {

    private final KeywordRepository keywordRepository = new FakeKeywordRepository();
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
}
