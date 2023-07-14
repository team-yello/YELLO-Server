package com.yello.server.domain.vote.entity;

import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Getter
@DynamicInsert
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

    @Builder
    public Vote(String answer, User sender, User receiver, Question question, Integer colorIndex) {
        this.answer = answer;
        this.sender = sender;
        this.receiver = receiver;
        this.question = question;
        this.colorIndex = colorIndex;
    }

    public void updateKeywordCheck() {
        this.isAnswerRevealed = true;
    }

    public void updateNameHintReveal(int nameHint) {
        this.nameHint = nameHint;
    }

    public static Vote createVote(String answer, User sender, User receiver, Question question, Integer colorIndex) {
        return new Vote(answer, sender, receiver, question, colorIndex);
    }
}