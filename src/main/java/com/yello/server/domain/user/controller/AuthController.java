package com.yello.server.domain.user.controller;

import com.yello.server.domain.user.service.AuthService;
import com.yello.server.global.common.SuccessCode;
import com.yello.server.global.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/valid")
    public BaseResponse<Boolean> getYelloIdValidation(@RequestParam("yelloId") String yelloId) {
        return BaseResponse.success(SuccessCode.YELLOID_VALIDATION_SUCCESS, authService.isYelloIdDuplicated(yelloId));
    }
}
