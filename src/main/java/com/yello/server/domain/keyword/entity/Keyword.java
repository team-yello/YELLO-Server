package com.yello.server.domain.keyword.entity;

import com.yello.server.domain.question.entity.Question;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keywordName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId")
    private Question question;

    public static Keyword of(String keywordName, Question question) {
        return Keyword.builder()
                .keywordName(keywordName)
                .question(question)
                .build();
    }
}
