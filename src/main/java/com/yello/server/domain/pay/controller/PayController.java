package com.yello.server.domain.pay.controller;

import static com.yello.server.global.common.SuccessCode.CREATE_PAY_COUNT;

import com.yello.server.domain.pay.dto.request.PayCountRequest;
import com.yello.server.domain.pay.service.PayService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    @PostMapping
    public BaseResponse postPayCount(@AccessTokenUser User user, @RequestBody PayCountRequest request) {
        payService.postPayCount(user.getId(), request.index());
        return BaseResponse.success(CREATE_PAY_COUNT);
    }
}
