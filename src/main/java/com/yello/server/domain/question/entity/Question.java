package com.yello.server.domain.question.entity;

import com.yello.server.domain.keyword.entity.Keyword;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
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


    public void addKeyword(Keyword keyword) {
        this.keywordList.add(keyword);
    }

}
