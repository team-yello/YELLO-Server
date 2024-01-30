package com.yello.server.domain.notice.service;

import static com.yello.server.global.common.factory.TimeFactory.compareNowAndEndData;

import com.yello.server.domain.notice.dto.NoticeDataResponse;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import com.yello.server.domain.notice.repository.NoticeRepository;
import com.yello.server.domain.user.repository.UserRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    public NoticeDataResponse findNotice(Long userId, NoticeType tag) {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        userRepository.findById(userId);
        Notice noticeData =
            noticeRepository.findTopNotice(tag).orElseGet(
                () -> Notice.builder().imageUrl("").redirectUrl("").title("").tag(tag).endDate(now)
                    .startDate(now).isAvailable(false).build());
        return NoticeDataResponse.of(noticeData,
            compareNowAndEndData(noticeData.getEndDate()) && noticeData.getIsAvailable());
    }


}
