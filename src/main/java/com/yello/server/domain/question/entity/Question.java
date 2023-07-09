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
    private String nameHead;

    @Column(nullable = false)
    private String nameFoot;

    @Column(nullable = false)
    private String keywordHead;

    @Column(nullable = false)
    private String keywordFoot;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer point;

    @Builder
    public Question(String nameHead, String nameFoot, String keywordHead, String keywordFoot, Integer point) {
        this.nameHead = nameHead;
        this.nameFoot = nameFoot;
        this.keywordHead = keywordHead;
        this.keywordFoot = keywordFoot;
        this.point = point;
    }
}
