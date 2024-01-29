package com.yello.server.domain.notice;

import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.repository.NoticeRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FakeNoticeRepository implements NoticeRepository {

    private List<Notice> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Optional<Notice> findTopNotice() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        return data.stream()
            .filter(notice ->
                notice.getIsAvailable().equals(true) &&
                    !notice.getStartDate().isAfter(now) &&
                    !notice.getEndDate().isBefore(now)
            )
            .sorted(Comparator.comparing(Notice::getEndDate).reversed())
            .findFirst();
    }

    @Override
    public Notice save(Notice notice) {
        final Notice newNotice = Notice.builder()
            .id(notice.getId() == null ? ++id : notice.getId())
            .endDate(notice.getEndDate())
            .imageUrl(notice.getImageUrl())
            .startDate(notice.getStartDate())
            .redirectUrl(notice.getRedirectUrl())
            .isAvailable(notice.getIsAvailable())
            .tag(notice.getTag())
            .title(notice.getTitle())
            .build();

        data.add(newNotice);
        return newNotice;
    }
}
