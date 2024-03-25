package com.yello.server.domain.statistics.service;

import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.statistics.dto.NewStatisticsUserGroupVO;
import com.yello.server.domain.statistics.dto.RevenueVO;
import com.yello.server.domain.statistics.dto.RevenueVO.RevenueItem;
import com.yello.server.domain.statistics.dto.SchoolAttackStatisticsVO;
import com.yello.server.domain.statistics.dto.SignUpVO;
import com.yello.server.domain.statistics.dto.VoteItem;
import com.yello.server.domain.statistics.dto.VoteVO;
import com.yello.server.domain.statistics.dto.response.StatisticsUserGroupSchoolAttackResponse;
import com.yello.server.domain.statistics.entity.StatisticsDaily;
import com.yello.server.domain.statistics.entity.StatisticsUserGroup;
import com.yello.server.domain.statistics.repository.StatisticsRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.global.common.util.ConstantUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final PurchaseRepository purchaseRepository;
    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public void writeUserGroupStatistics() {
        final LocalDateTime voteStartAt = LocalDateTime.of(2023, 12, 1, 0, 0, 0);
        final List<NewStatisticsUserGroupVO> newStatistics = statisticsRepository.getUserGroupNewStatistics(
            voteStartAt);

        for (NewStatisticsUserGroupVO newStatistic : newStatistics) {
            final Optional<StatisticsUserGroup> groupStatistics = statisticsRepository.findByUserGroupName(
                newStatistic.userGroupName());

            if (groupStatistics.isEmpty()) {
                statisticsRepository.save(StatisticsUserGroup.builder()
                    .groupName(newStatistic.userGroupName())
                    .userCount(newStatistic.userCount())
                    .voteCount(newStatistic.voteCount())
                    .alpha(0L)
                    .rankNumber(newStatistic.rankNumber())
                    .prevUserCount(0L)
                    .prevVoteCount(0L)
                    .prevAlpha(0L)
                    .prevRankNumber(0L)
                    .build());
            } else {
                groupStatistics.get().update(
                    newStatistic.voteCount(),
                    newStatistic.userCount(),
                    0L,
                    newStatistic.rankNumber(),
                    groupStatistics.get().getVoteCount(),
                    groupStatistics.get().getUserCount(),
                    groupStatistics.get().getAlpha(),
                    groupStatistics.get().getRankNumber()
                );
            }
        }
    }

    public StatisticsUserGroupSchoolAttackResponse getSchoolAttackStatistics(Pageable pageable) {
        final List<StatisticsUserGroup> statistics = statisticsRepository.getSchoolAttackStatistics(
            pageable);
        final Long counted = statisticsRepository.countSchoolAttackStatistics();
        final LocalDateTime lastUpdatedAt = statisticsRepository.getSchoolAttackLastUpdatedAt();
        long pageSize = pageable.getPageSize();

        return StatisticsUserGroupSchoolAttackResponse.builder()
            .pageCount(counted % pageSize == 0 ? counted / pageSize : counted / pageSize + 1)
            .totalCount(counted)
            .updatedAt(lastUpdatedAt)
            .statisticsList(statistics.stream().map(SchoolAttackStatisticsVO::of).toList())
            .build();
    }

    public SchoolAttackStatisticsVO getSchoolAttackStatisticsByGroupName(String groupName) {
        return SchoolAttackStatisticsVO.of(
            statisticsRepository.getByUserGroupName(groupName)
        );
    }

    public StatisticsUserGroupSchoolAttackResponse getSchoolAttackStatisticsLikeGroupName(String groupName,
        Pageable pageable) {
        final List<StatisticsUserGroup> statistics = statisticsRepository.getSchoolAttackStatisticsContaining(
            groupName, pageable);
        final Long counted = statisticsRepository.countSchoolAttackStatisticsContaining(groupName);
        final LocalDateTime lastUpdatedAt = statisticsRepository.getSchoolAttackLastUpdatedAt();
        long pageSize = pageable.getPageSize();

        return StatisticsUserGroupSchoolAttackResponse.builder()
            .pageCount(counted % pageSize == 0 ? counted / pageSize : counted / pageSize + 1)
            .totalCount(counted)
            .updatedAt(lastUpdatedAt)
            .statisticsList(statistics.stream().map(SchoolAttackStatisticsVO::of).toList())
            .build();
    }

    @Transactional
    public StatisticsDaily writeDailyServiceStatistics(LocalDate startAt, LocalDate endAt) {
        final LocalDateTime startTime = startAt.atStartOfDay();
        final LocalDateTime endTime = endAt.atStartOfDay();

        final Long count = userRepository.count(startTime, endTime);
        final Long countFemale = userRepository.countAllByGender(Gender.FEMALE, startTime, endTime);
        final Long countMale = userRepository.countAllByGender(Gender.MALE, startTime, endTime);
        final Long countDeletedAt = userRepository.countDeletedAt(startTime, endTime);
        final SignUpVO signUpVO = SignUpVO.builder()
            .count(count)
            .femaleCount(countFemale)
            .maleCount(countMale)
            .deleteAtCount(countDeletedAt)
            .build();

        List<RevenueItem> revenueItemList = new ArrayList<>();
        for (Gateway gateway : Gateway.values()) {
            for (ProductType productType : ProductType.values()) {
                final Long purchaseCount = purchaseRepository.countByStartAt(gateway, productType, startTime, endTime);
                final Long totalPrice = purchaseRepository.countPriceByStartAt(gateway, productType, startTime,
                    endTime);

                revenueItemList.add(RevenueItem.builder()
                    .gateway(gateway)
                    .productType(productType)
                    .purchaseCount(purchaseCount)
                    .totalPrice(totalPrice)
                    .build());
            }
        }
        final RevenueVO revenueVO = RevenueVO.builder()
            .revenueItemList(revenueItemList)
            .build();

        List<VoteItem> voteItemList = voteRepository.countDailyVoteData(startTime, endTime);
        final VoteVO voteVO = VoteVO.builder()
            .voteItemList(voteItemList)
            .build();

        final StatisticsDaily saved = statisticsRepository.save(StatisticsDaily.builder()
            .startAt(startAt.atStartOfDay(ConstantUtil.GlobalZoneId))
            .endAt(endAt.atStartOfDay(ConstantUtil.GlobalZoneId))
            .signUp(signUpVO)
            .revenue(revenueVO)
            .vote(voteVO)
            .build());

        return statisticsRepository.getById(saved.getId());
    }
}
