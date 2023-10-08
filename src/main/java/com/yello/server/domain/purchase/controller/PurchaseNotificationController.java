package com.yello.server.domain.purchase.controller;

import static com.yello.server.global.common.SuccessCode.POST_APPLE_NOTIFICATION_SUCCESS;
import static com.yello.server.global.common.SuccessCode.POST_GOOGLE_NOTIFICATION_SUCCESS;

import com.yello.server.domain.purchase.dto.request.AppleNotificationRequest;
import com.yello.server.domain.purchase.service.PurchaseService;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.global.common.dto.EmptyObject;
import com.yello.server.infrastructure.slack.annotation.SlackApplePurchaseNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PurchaseNotificationController {

    private final PurchaseService purchaseService;

    @PostMapping("/v2/apple/notifications")
    @SlackApplePurchaseNotification
    public BaseResponse appleNotification(
        @RequestBody AppleNotificationRequest request
    ) {
        purchaseService.appleNotification(request);
        return BaseResponse.success(POST_APPLE_NOTIFICATION_SUCCESS);
    }

    @PostMapping("/v2/google/notifications")
    public BaseResponse<EmptyObject> googleNotification(
        @RequestBody Object request
    ) {
        System.out.println("request = " + request);
        return BaseResponse.success(POST_GOOGLE_NOTIFICATION_SUCCESS);
    }
}
