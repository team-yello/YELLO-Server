package com.yello.server.global.common.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("ListFactory 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class ListFactoryTest {

    @Test
    void null이_없는_리스트_변환에_성공합니다() {
        // given
        List<Optional<Integer>> optionalList = new ArrayList<>();
        optionalList.add(Optional.of(1));
        optionalList.add(Optional.empty());
        optionalList.add(Optional.of(3));
        optionalList.add(Optional.empty());
        optionalList.add(Optional.of(5));

        // when
        List<Integer> result = ListFactory.toNonNullableList(optionalList);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(1);
        assertThat(result.get(1)).isEqualTo(3);
        assertThat(result.get(2)).isEqualTo(5);
    }


}
