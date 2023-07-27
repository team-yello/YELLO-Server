package com.yello.server.domain.pay.controller;

import com.yello.server.domain.pay.dto.request.PayCountRequest;
import com.yello.server.domain.pay.sevice.PayService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.yello.server.global.common.SuccessCode.CREATE_PAY_COUNT;

@Tag(name = "05. Pay [임시]")
@RestController
@RequestMapping("api/v1/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    @Operation(summary = "결제 전환율 체크 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public BaseResponse postPayCount(
            @AccessTokenUser User user,
            @RequestBody PayCountRequest request) {
        payService.postPayCount(user.getId(), request.index());
        return BaseResponse.success(CREATE_PAY_COUNT);
    }
}
