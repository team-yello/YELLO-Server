package com.yello.server.domain.purchase.medium;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.authorization.filter.JwtExceptionFilter;
import com.yello.server.domain.authorization.filter.JwtFilter;
import com.yello.server.domain.purchase.controller.PurchaseController;
import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import com.yello.server.domain.purchase.dto.request.AppleInAppRefundRequest;
import com.yello.server.domain.purchase.dto.request.GoogleSubscriptionGetRequest;
import com.yello.server.domain.purchase.dto.request.GoogleTicketGetRequest;
import com.yello.server.domain.purchase.dto.response.GoogleSubscriptionGetResponse;
import com.yello.server.domain.purchase.dto.response.GoogleTicketGetResponse;
import com.yello.server.domain.purchase.dto.response.UserSubscribeNeededResponse;
import com.yello.server.domain.purchase.service.PurchaseService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.exception.ControllerExceptionAdvice;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataUtil;
import com.yello.server.util.WithAccessTokenUser;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureRestDocs
@WebMvcTest(
    controllers = PurchaseController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtExceptionFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ControllerExceptionAdvice.class)
    })
@WithAccessTokenUser
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Purchase 컨트롤러에서")
class PurchaseControllerTest {

    final String[] excludeRequestHeaders = {"X-CSRF-TOKEN", "Host"};
    final String[] excludeResponseHeaders = {"X-Content-Type-Options", "X-XSS-Protection", "Cache-Control", "Pragma",
        "Expires", "X-Frame-Options", "Content-Length"};

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseService purchaseService;

    private TestDataUtil testDataUtil = new TestDataEntityUtil();
    private User user;
    private User target;

    @BeforeEach
    void init() {
        user = testDataUtil.generateUser(1L, 1L);
        target = testDataUtil.generateUser(2L, 1L);
    }

    @Test
    void Apple_구독_구매_검증에_성공합니다() throws Exception {
        // given
        final AppleTransaction appleTransaction = AppleTransaction.builder()
            .transactionId("transactionId")
            .productId("productId")
            .build();

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/purchase/apple/verify/subscribe")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appleTransaction)))
            .andDo(print())
            .andDo(document("api/v1/purchase/verifyAppleSubscriptionTransaction",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());

        verify(purchaseService, times(1))
            .verifyAppleSubscriptionTransaction(anyLong(), any(AppleTransaction.class));
    }

    @Test
    void Apple_열람권_구매_검증에_성공합니다() throws Exception {
        // given
        final AppleTransaction appleTransaction = AppleTransaction.builder()
            .transactionId("transactionId")
            .productId("productId")
            .build();

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/purchase/apple/verify/ticket")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appleTransaction)))
            .andDo(print())
            .andDo(document("api/v1/purchase/verifyAppleTicketTransaction",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());

        verify(purchaseService, times(1))
            .verifyAppleTicketTransaction(anyLong(), any(AppleTransaction.class));
    }

    @Test
    void Google_구독_구매_검증에_성공합니다() throws Exception {
        // given
        final GoogleSubscriptionGetRequest googleSubscriptionGetRequest = GoogleSubscriptionGetRequest.builder()
            .orderId("orderId")
            .packageName("packageName")
            .productId("productId")
            .purchaseTime(1L)
            .purchaseState(1)
            .purchaseToken("purchaseToken")
            .quantity(1)
            .autoRenewing(true)
            .acknowledged(true)
            .build();

        final GoogleSubscriptionGetResponse response = GoogleSubscriptionGetResponse.of(
            "productId");

        given(purchaseService.verifyGoogleSubscriptionTransaction(anyLong(), any(GoogleSubscriptionGetRequest.class)))
            .willReturn(response);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/purchase/google/verify/subscribe")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(googleSubscriptionGetRequest)))
            .andDo(print())
            .andDo(document("api/v1/purchase/verifyGoogleSubscriptionTransaction",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void Google_열람권_구매_검증에_성공합니다() throws Exception {
        // given
        final GoogleTicketGetRequest googleTicketGetRequest = GoogleTicketGetRequest.builder()
            .orderId("orderId")
            .packageName("packageName")
            .productId("productId")
            .purchaseTime(1L)
            .purchaseState(1)
            .purchaseToken("purchaseToken")
            .quantity(1)
            .acknowledged(true)
            .build();

        final GoogleTicketGetResponse response = GoogleTicketGetResponse.of(
            "productId",
            user
        );

        given(purchaseService.verifyGoogleTicketTransaction(anyLong(), any(GoogleTicketGetRequest.class)))
            .willReturn(response);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/purchase/google/verify/ticket")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(googleTicketGetRequest)))
            .andDo(print())
            .andDo(document("api/v1/purchase/verifyGoogleTicketTransaction",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 구독_연장_유도_필요_여부_조회에_성공합니다() throws Exception {
        // given
        final UserSubscribeNeededResponse response = UserSubscribeNeededResponse.of(
            user,
            false
        );

        given(purchaseService.getUserSubscribe(any(User.class), any(LocalDateTime.class)))
            .willReturn(response);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/purchase/subscribe")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token"))
            .andDo(print())
            .andDo(document("api/v1/purchase/getUserSubscribeNeeded",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void Apple_환불에_성공합니다() throws Exception {
        // given
        doNothing()
            .when(purchaseService)
            .refundInAppApple(anyLong(), any(AppleInAppRefundRequest.class));

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/purchase/apple/refund")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token"))
            .andDo(print())
            .andDo(document("api/v1/purchase/refundInAppApple",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 구독_상태_및_구독권_개수_조회에_성공합니다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/purchase")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token"))
            .andDo(print())
            .andDo(document("api/v1/purchase/getUserPurchaseInfo",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
