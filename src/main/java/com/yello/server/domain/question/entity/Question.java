package com.yello.server.domain.question.entity;

import com.yello.server.domain.keyword.entity.Keyword;
import java.text.MessageFormat;
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
import lombok.val;

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

    private static String deleteBracket(String target) {
        val slashIndex = target.indexOf('/');
        return slashIndex!=-1 ? target.substring(slashIndex + 1) : target;
    }

    public static Question of(String nameHead, String nameFoot, String keywordHead, String keywordFoot) {
        return Question.builder()
            .nameHead(nameHead)
            .nameFoot(nameFoot)
            .keywordHead(keywordHead)
            .keywordFoot(keywordFoot)
            .build();
    }

    public void addKeyword(Keyword keyword) {
        this.keywordList.add(keyword);
    }

    public String toNotificationSentence() {
        val foot = deleteBracket(this.nameFoot);
        if (this.nameHead==null) {
            return MessageFormat.format("너{0} ... ", foot);
        }
        return MessageFormat.format("{0} 너{1} ... ", this.nameHead, foot);
    }
}
