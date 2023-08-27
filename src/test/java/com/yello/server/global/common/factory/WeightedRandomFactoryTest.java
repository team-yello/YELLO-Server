package com.yello.server.global.common.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("WeightedRandomFactory 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class WeightedRandomFactoryTest {

    @Test
    void 가중치_랜덤값_생성에_성공합니다() {
        // given
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        // when
        Integer result = WeightedRandomFactory.randomPoint();

        // then
        assertThat(result).isBetween(5, 25);
        assertThat(random.nextDouble()).isBetween(0.0, 1.0);
    }
}