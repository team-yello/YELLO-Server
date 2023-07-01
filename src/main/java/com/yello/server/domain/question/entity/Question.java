package com.yello.server.domain.question.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String head;

    @Column(nullable = false)
    private String foot;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer score;

    @Builder
    public Question(String head, String foot, Integer score) {
        this.head = head;
        this.foot = foot;
        this.score = score;
    }
}
