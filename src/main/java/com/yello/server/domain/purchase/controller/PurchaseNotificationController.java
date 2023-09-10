package com.yello.server.domain.purchase.controller;

import static com.yello.server.global.common.SuccessCode.POST_APPLE_NOTIFICATION_SUCCESS;

import com.yello.server.domain.purchase.dto.request.AppleNotificationRequest;
import com.yello.server.domain.purchase.service.PurchaseService;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.infrastructure.slack.annotation.SlackPurchaseNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PurchaseNotificationController {

    private final PurchaseService purchaseService;

    @PostMapping("/v2/apple/notifications")
    @SlackPurchaseNotification
    public BaseResponse appleNotification(
        @RequestBody AppleNotificationRequest request
    ) {
        purchaseService.appleNotification(request);
        return BaseResponse.success(POST_APPLE_NOTIFICATION_SUCCESS);
    }
}
