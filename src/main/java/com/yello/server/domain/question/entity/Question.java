package com.yello.server.domain.question.entity;

import com.yello.server.domain.keyword.entity.Keyword;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private final List<Keyword> keywordList = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String nameHead;
    @Column
    private String nameFoot;
    @Column
    private String keywordHead;
    @Column
    private String keywordFoot;

    @Builder
    public Question(String nameHead, String nameFoot, String keywordHead, String keywordFoot) {
        this.nameHead = nameHead;
        this.nameFoot = nameFoot;
        this.keywordHead = keywordHead;
        this.keywordFoot = keywordFoot;
    }
}
