package com.yello.server.entity.keyword;

import com.yello.server.entity.AuditingTimeEntity;
import com.yello.server.entity.question.Question;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keywordName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId")
    private Question question;

    @Builder
    public Keyword(String keywordName, Question question) {
        this.keywordName = keywordName;
        this.question = question;
    }
}
