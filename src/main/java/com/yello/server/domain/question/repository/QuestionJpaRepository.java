package com.yello.server.domain.question.repository;

import com.yello.server.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<Question, Long> {

}
