package com.yello.server.global.common.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("TimeFactory 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class TimeFactoryTest {

    @Test
    void 시간을_문자열_형식으로_포맷팅에_성공합니다() {
        // given
        final LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 14, 0, 0);

        final LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, currentDateTime);
        long seconds = duration.getSeconds();
        String expect = "";

        if (seconds < 60) {
            expect = seconds + "초 전";
        } else if ((seconds /= 60) < 60) {
            expect = seconds + "분 전";
        } else if ((seconds /= 60) < 24) {
            expect = (seconds) + "시간 전";
        }

        expect = (seconds / 24) + "일 전";

        // when
        String result = TimeFactory.toFormattedString(localDateTime);

        // then
        assertThat(result).isEqualTo(expect);
    }
}