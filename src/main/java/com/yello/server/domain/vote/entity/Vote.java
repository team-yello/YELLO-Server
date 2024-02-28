package com.yello.server.domain.vote.entity;

import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAnswerRevealed = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "senderId")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "receiverId")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
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