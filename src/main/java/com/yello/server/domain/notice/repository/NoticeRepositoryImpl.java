package com.yello.server.domain.notice.repository;

import static com.yello.server.domain.notice.entity.QNotice.notice;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_NOTICE_EXCEPTION;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import com.yello.server.domain.notice.exception.NoticeNotFoundException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeRepositoryImpl implements NoticeRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final NoticeJpaRepository noticeJpaRepository;

    @Override
    public Optional<Notice> findTopNotice(NoticeType tag) {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(notice)
            .where(notice.isAvailable.eq(true)
                .and(notice.startDate.loe(zonedDateTime))
                .and(notice.endDate.goe(zonedDateTime))
                .and(notice.tag.eq(tag)))
            .orderBy(notice.endDate.desc())
            .fetchFirst());
    }

    @Override
    public List<Notice> findAll() {
        return jpaQueryFactory.selectFrom(notice).fetch();
    }

    @Override
    public Notice getById(Long id) {
        return noticeJpaRepository.findById(id)
            .orElseThrow(() -> new NoticeNotFoundException(NOT_FOUND_NOTICE_EXCEPTION));
    }

    @Override
    public Notice save(Notice notice) {
        return noticeJpaRepository.save(notice);
    }

    @Override
    public void update(Notice newNotice) {
        jpaQueryFactory.update(notice)
            .set(notice.imageUrl, newNotice.getImageUrl())
            .set(notice.redirectUrl, newNotice.getRedirectUrl())
            .set(notice.startDate, newNotice.getStartDate())
            .set(notice.endDate, newNotice.getEndDate())
            .set(notice.isAvailable, newNotice.getIsAvailable())
            .set(notice.tag, newNotice.getTag())
            .set(notice.title, newNotice.getTitle())
            .where(notice.id.eq(newNotice.getId()))
            .execute();
    }

}
