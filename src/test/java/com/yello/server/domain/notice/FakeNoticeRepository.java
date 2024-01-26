package com.yello.server.domain.notice;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_NOTICE_EXCEPTION;

import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.exception.NoticeNotFoundException;
import com.yello.server.domain.notice.repository.NoticeRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FakeNoticeRepository implements NoticeRepository {

    private List<Notice> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Notice getTopNotice() {
        return data.stream()
            .sorted(Comparator.comparing(Notice::getEndDate).reversed())
            .findFirst()
            .orElseThrow(() -> new NoticeNotFoundException(NOT_FOUND_NOTICE_EXCEPTION));
    }

    @Override
    public Notice save(Notice notice) {
        final Notice newNotice = Notice.builder()
            .id(notice.getId()==null ? ++id : notice.getId())
            .endDate(notice.getEndDate())
            .imageUrl(notice.getImageUrl())
            .startDate(notice.getStartDate())
            .redirectUrl(notice.getRedirectUrl())
            .isAvailable(notice.getIsAvailable())
            .build();

        data.add(newNotice);
        return newNotice;
    }
}
