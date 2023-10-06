package com.yello.server.domain.question.repository;

import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionGroupType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionGroupTypeJpaRepository extends JpaRepository<QuestionGroupType, Long> {

    @Query("select q from Question q "
        + "join QuestionGroupType qgy on q.id=qgy.question.id "
        + "where qgy.userGroupType = :userGroupType")
    List<Question> findQuestionByGroupType(@Param("userGroupType") UserGroupType userGroupType);

}
