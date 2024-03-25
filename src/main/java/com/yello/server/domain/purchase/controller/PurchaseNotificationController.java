package com.yello.server.domain.purchase.controller;

import static com.yello.server.global.common.SuccessCode.POST_APPLE_NOTIFICATION_SUCCESS;
import static com.yello.server.global.common.SuccessCode.POST_GOOGLE_NOTIFICATION_SUCCESS;

import com.yello.server.domain.purchase.dto.request.AppleNotificationRequest;
import com.yello.server.domain.purchase.dto.request.GooglePubSubNotificationRequest;
import com.yello.server.domain.purchase.dto.request.google.DeveloperNotification;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.service.PurchaseService;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.global.common.dto.EmptyObject;
import com.yello.server.infrastructure.slack.dto.response.SlackChannel;
import com.yello.server.infrastructure.slack.service.SlackService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
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
    private final SlackService slackService;

    @PostMapping("/v2/apple/notifications")
    public BaseResponse appleNotification(@RequestBody AppleNotificationRequest request,
        HttpServletRequest servletRequest) throws IOException, URISyntaxException {
        purchaseService.appleNotification(request);

        slackService.sendAppleStateChangedMessage(SlackChannel.PURCHASE, servletRequest);
        return BaseResponse.success(POST_APPLE_NOTIFICATION_SUCCESS);
    }

    @PostMapping("/v2/google/notifications")
    public BaseResponse<EmptyObject> googleNotification(
        @RequestBody GooglePubSubNotificationRequest request, HttpServletRequest servletRequest
    ) throws IOException, URISyntaxException {
        final Map.Entry<DeveloperNotification, Optional<Purchase>> notification = purchaseService.googleNotification(
            request);

        slackService.sendGoogleStateChangedMessage(SlackChannel.PURCHASE, servletRequest, notification);
        return BaseResponse.success(POST_GOOGLE_NOTIFICATION_SUCCESS, EmptyObject.builder().build());
    }
}
