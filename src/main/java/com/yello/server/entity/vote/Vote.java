package com.yello.server.entity.vote;

import com.yello.server.entity.AuditingTimeEntity;
import com.yello.server.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;

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
    private Boolean inAnswerHint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver")
    private User receiver;

    @Builder
    public Vote(String head, String answer, String foot, Integer nameHint, Boolean inAnswerHint, User sender, User receiver) {
        this.head = head;
        this.answer = answer;
        this.foot = foot;
        this.nameHint = nameHint;
        this.inAnswerHint = inAnswerHint;
        this.sender = sender;
        this.receiver = receiver;
    }
}
