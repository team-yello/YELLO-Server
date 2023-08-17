package com.yello.server.small.domain.purchase;

import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.purchase.dto.response.UserSubscribeNeededResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.purchase.service.PurchaseService;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.small.domain.user.FakeUserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PurchaseTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final PurchaseRepository purchaseRepository = new FakePurchaseRepository();
    private PurchaseService purchaseService;

    @BeforeEach
    void init() {
        this.purchaseService = PurchaseService.builder()
            .userRepository(userRepository)
            .purchaseRepository(purchaseRepository)
            .build();

        School school = School.builder()
            .schoolName("Test School")
            .departmentName("Testing")
            .build();
        final User user = User.builder()
            .id(1L)
            .recommendCount(0L).name("test")
            .yelloId("yelloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image").uuid("1234")
            .deletedAt(null).group(school)
            .groupAdmissionYear(20).email("test@test.com")
            .subscribe(Subscribe.CANCELED)
            .build();
        userRepository.save(user);

        purchaseRepository.save(Purchase.builder()
            .id(1L)
            .price(1000)
            .user(user)
            .gateway(Gateway.GOOGLE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .productType(ProductType.YELLO_PLUS)
            .build());
    }

    @Test
    void 구독_연장_유도_필요_여부_확인_조회에_성공합니다() {
        // given
        Long userId = 1L;

        // when
        final User user = userRepository.getById(userId);
        final UserSubscribeNeededResponse response = purchaseService.getUserSubscribe(user,
            LocalDateTime.now().plusDays(1).minusMinutes(1));

        // then
        assertThat(response.subscribe()).isEqualTo(user.getSubscribe());
        assertThat(response.isSubscribeNeeded()).isEqualTo(true);
    }
}
