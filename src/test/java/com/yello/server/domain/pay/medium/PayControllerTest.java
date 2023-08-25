package com.yello.server.domain.pay.medium;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.yello.server.domain.pay.controller.PayController;
import com.yello.server.domain.pay.dto.request.PayCountRequest;
import com.yello.server.domain.pay.service.PayService;
import com.yello.server.global.exception.ControllerExceptionAdvice;
import com.yello.server.util.WithAccessTokenUser;
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
    controllers = PayController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtExceptionFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ControllerExceptionAdvice.class)
    })
@WithAccessTokenUser
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Pay 컨트롤러에서")
class PayControllerTest {

    final String[] excludeRequestHeaders = {"X-CSRF-TOKEN", "Host"};
    final String[] excludeResponseHeaders = {"X-Content-Type-Options", "X-XSS-Protection", "Cache-Control", "Pragma",
        "Expires", "X-Frame-Options", "Content-Length"};

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PayService payService;

    @Test
    void 결제_전환율_체크에_성공합니다() throws Exception {
        // given
        final PayCountRequest request = PayCountRequest.builder()
            .index(1)
            .build();

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/pay")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andDo(document("api/v1/pay/postPayCount",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());

        verify(payService, times(1))
            .postPayCount(anyLong(), anyInt());
    }

}
