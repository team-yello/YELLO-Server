package com.yello.server.domain.question.repository;

import com.yello.server.domain.question.entity.Question;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionJpaRepository extends JpaRepository<Question, Long> {

    @Query("select q from Question q "
        + "where (COALESCE(:nameHead, '') = '' OR q.nameHead is null OR q.nameHead = :nameHead) "
        + "and (COALESCE(:nameFoot, '') = '' OR q.nameFoot is null OR q.nameFoot = :nameFoot) "
        + "and (COALESCE(:keywordHead, '') = '' OR q.keywordHead is null OR q.keywordHead = :keywordHead) "
        + "and (COALESCE(:keywordFoot, '') = '' OR q.keywordFoot is null OR q.keywordFoot = :keywordFoot)")
    Optional<Question> findByQuestionContent(@Param("nameHead") String nameHead,
        @Param("nameFoot") String nameFoot,
        @Param("keywordHead") String keywordHead, @Param("keywordFoot") String keywordFoot);
}