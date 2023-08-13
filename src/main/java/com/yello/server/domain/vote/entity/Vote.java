package com.yello.server.domain.vote.entity;

import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import java.util.Random;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@SuperBuilder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String answer;

    @ColumnDefault("-1")
    @Column(nullable = false)
    private Integer nameHint;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isAnswerRevealed;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverId")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId")
    private Question question;

    @Column(nullable = false)
    private Integer colorIndex;

    public static Vote of(String answer, User sender, User receiver, Question question,
        Integer colorIndex) {
        return Vote.builder()
            .answer(answer)
            .sender(sender)
            .receiver(receiver)
            .question(question)
            .colorIndex(colorIndex)
            .build();
    }

    public static Vote createVote(String answer, User sender, User receiver, Question question,
        Integer colorIndex) {
        return Vote.of(answer, sender, receiver, question, colorIndex);
    }

    public static Vote createFirstVote(String answer, User sender, User receiver, Question question) {
        Random random = new Random();

        return Vote.builder()
            .answer(answer)
            .nameHint(-3)
            .isAnswerRevealed(true)
            .isRead(false)
            .sender(sender)
            .receiver(receiver)
            .question(question)
            .colorIndex(random.nextInt(12) + 1)
            .build();
    }

    public void checkKeyword() {
        this.isAnswerRevealed = true;
    }

    public void checkNameIndexOf(int nameHint) {
        this.nameHint = nameHint;
    }

    public void read() {
        this.isRead = true;
    }
}