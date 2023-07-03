package com.yello.server.domain.vote.entity;

import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String head;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private String foot;

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
    @JoinColumn(name = "receiver")
    private User receiver;

    @Builder
    public Vote(String head, String answer, String foot, Integer nameHint, Boolean isAnswerRevealed, Boolean isRead,
        User sender,
        User receiver) {
        this.head = head;
        this.answer = answer;
        this.foot = foot;
        this.nameHint = nameHint;
        this.isAnswerRevealed = isAnswerRevealed;
        this.isRead = isRead;
        this.sender = sender;
        this.receiver = receiver;
    }
}
