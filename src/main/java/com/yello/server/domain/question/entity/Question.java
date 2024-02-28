package com.yello.server.domain.question.entity;

import com.yello.server.domain.keyword.entity.Keyword;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        return slashIndex != -1 ? target.substring(slashIndex + 1) : target;
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
        final String nameFootPart = deleteBracket(this.nameFoot);
        final String nameHeadPart = (this.nameHead != null) ? MessageFormat.format("{0} ", this.nameHead) : "";
        final String keywordHeadPart = (this.keywordHead != null) ? MessageFormat.format(" {0}", this.keywordHead) : "";

        return MessageFormat.format("{0}너{1}{2} ...", nameHeadPart, nameFootPart, keywordHeadPart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Question question = (Question) o;
        return Objects.equals(nameHead, question.nameHead)
            && Objects.equals(nameFoot, question.nameFoot)
            && Objects.equals(keywordHead, question.keywordHead)
            && Objects.equals(keywordFoot, question.keywordFoot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keywordList, id, nameHead, nameFoot, keywordHead, keywordFoot);
    }
}
