package com.yello.server.global.common.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

@DisplayName("PaginationFactory 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class PaginationFactoryTest {

    private final int PAGE_LIMIT = 100;
    private final int PAGE_LIMIT_TEN = 10;

    @Test
    void Pageable_객체_생성에_성공합니다() {
        // given
        final Integer page = 1;

        // when
        final Pageable pageable = PaginationFactory.createPageable(page);

        // then
        assertThat(pageable.getPageNumber()).isEqualTo(page);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_LIMIT);
    }

    @Test
    void 이름으로_정렬된_Pageable_객체_생성에_성공합니다() {
        // given
        final Integer page = 1;

        // when
        final Pageable pageable = PaginationFactory.createPageableByNameSort(page);

        // then
        assertThat(pageable.getPageNumber()).isEqualTo(page);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_LIMIT);
        assertThat(pageable.getSort().getOrderFor("name").getDirection()).isEqualTo(Direction.ASC);
    }

    @Test
    void Page_Size_10_고정_Pageable_객체_생성에_성공합니다() {
        // given
        final Integer page = 1;

        // when
        final Pageable pageable = PaginationFactory.createPageable(page);

        // then
        assertThat(pageable.getPageNumber()).isEqualTo(page);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_LIMIT);
    }

    @Test
    void Page_객체_생성에_성공합니다() {
        // given
        final Integer page = 1;
        final Pageable pageable = PageRequest.of(page, PAGE_LIMIT_TEN);
        List<Integer> dataList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            dataList.add(i);
        }

        // when
        Page<Integer> resultPage = PaginationFactory.getPage(dataList, pageable);

        // then
        assertThat(pageable.getPageNumber()).isEqualTo(page);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_LIMIT_TEN);

        assertThat(resultPage.getTotalElements()).isEqualTo(20);
        assertThat(resultPage.getSize()).isEqualTo(PAGE_LIMIT_TEN);
        assertThat(resultPage.getTotalPages()).isEqualTo(2);

        List<Integer> content = resultPage.getContent();
        assertThat(content).hasSize(10);
        assertThat(content.get(0)).isEqualTo(11);
        assertThat(content.get(9)).isEqualTo(20);
    }
}
