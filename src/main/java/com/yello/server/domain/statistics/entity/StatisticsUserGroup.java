package com.yello.server.domain.statistics.entity;

import com.yello.server.global.common.dto.AuditingTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@SuperBuilder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "statistics_user_group__group_name__unique",
            columnNames = {"groupName"}
        ),
    }
)
public class StatisticsUserGroup extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @JoinColumn(
        name = "groupName",
        foreignKey = @ForeignKey(
            value = ConstraintMode.NO_CONSTRAINT,
            foreignKeyDefinition = "FOREIGN KEY (group_name) REFERENCES user_group (group_name)"
            /**
             * NOTE ForeignKey Constraint는 직접 설정해주어야한다.
            */
        )
    )
    private String groupName;

    @Column
    private Long voteCount;

    @Column
    private Long userCount;

    @Column
    private Long alpha;

    @Column
    private Long rankNumber;

    @Column
    private Long prevVoteCount;

    @Column
    private Long prevUserCount;

    @Column
    private Long prevAlpha;

    @Column
    private Long prevRankNumber;

    public void update(Long voteCount, Long userCount, Long alpha, Long rankNumber, Long prevVoteCount,
        Long prevUserCount,
        Long prevAlpha, Long prevRankNumber) {
        this.voteCount = voteCount;
        this.userCount = userCount;
        this.alpha = alpha;
        this.rankNumber = rankNumber;
        this.prevVoteCount = prevVoteCount;
        this.prevUserCount = prevUserCount;
        this.prevAlpha = prevAlpha;
        this.prevRankNumber = prevRankNumber;
    }

    public void updateRankNumber(Long rankNumber, Long prevRankNumber) {
        this.rankNumber = rankNumber;
        this.prevRankNumber = prevRankNumber;
    }
}
