package com.yello.server.domain.question.entity;


import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.group.entity.UserGroupTypeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionGroupType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Convert(converter = UserGroupTypeConverter.class)
    @ColumnDefault("\"UNIVERSITY\"")
    private UserGroupType userGroupType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId")
    private Question question;

    public static QuestionGroupType of(UserGroupType userGroupType, Question question) {
        return QuestionGroupType.builder()
            .userGroupType(userGroupType)
            .question(question)
            .build();
    }

}
