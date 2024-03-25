package com.yello.server.domain.purchase.controller;

import static com.yello.server.global.common.SuccessCode.GOOGLE_PURCHASE_INAPP_VERIFY_SUCCESS;
import static com.yello.server.global.common.SuccessCode.GOOGLE_PURCHASE_SUBSCRIPTION_VERIFY_SUCCESS;
import static com.yello.server.global.common.SuccessCode.USER_PURCHASE_INFO_READ_SUCCESS;
import static com.yello.server.global.common.SuccessCode.USER_SUBSCRIBE_NEEDED_READ_SUCCESS;
import static com.yello.server.global.common.SuccessCode.VERIFY_RECEIPT_SUCCESS;

import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import com.yello.server.domain.purchase.dto.request.AppleInAppRefundRequest;
import com.yello.server.domain.purchase.dto.request.GoogleSubscriptionGetRequest;
import com.yello.server.domain.purchase.dto.request.GoogleTicketGetRequest;
import com.yello.server.domain.purchase.dto.response.GoogleSubscriptionGetResponse;
import com.yello.server.domain.purchase.dto.response.GoogleTicketGetResponse;
import com.yello.server.domain.purchase.dto.response.UserPurchaseInfoResponse;
import com.yello.server.domain.purchase.dto.response.UserSubscribeNeededResponse;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.service.PurchaseService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.infrastructure.slack.dto.response.SlackChannel;
import com.yello.server.infrastructure.slack.service.SlackService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final SlackService slackService;

    @PostMapping("/apple/verify/subscribe")
    public BaseResponse verifyAppleSubscriptionTransaction(
        @RequestBody AppleTransaction appleTransaction,
        @AccessTokenUser User user,
        HttpServletRequest servletRequest
    ) throws IOException {
        purchaseService.verifyAppleSubscriptionTransaction(user.getId(), appleTransaction);
        final Purchase purchase = purchaseService.getByTransactionId(appleTransaction.transactionId());

        slackService.sendPurchaseMessage(SlackChannel.PURCHASE, servletRequest, purchase);
        return BaseResponse.success(VERIFY_RECEIPT_SUCCESS);
    }

    @PostMapping("/apple/verify/ticket")
    public BaseResponse verifyAppleTicketTransaction(
        @RequestBody AppleTransaction appleTransaction,
        @AccessTokenUser User user,
        HttpServletRequest servletRequest
    ) throws IOException {
        purchaseService.verifyAppleTicketTransaction(user.getId(), appleTransaction);
        final Purchase purchase = purchaseService.getByTransactionId(appleTransaction.transactionId());

        slackService.sendPurchaseMessage(SlackChannel.PURCHASE, servletRequest, purchase);
        return BaseResponse.success(VERIFY_RECEIPT_SUCCESS);
    }

    @PostMapping("/google/verify/subscribe")
    public BaseResponse<GoogleSubscriptionGetResponse> verifyGoogleSubscriptionTransaction(
        @AccessTokenUser User user,
        @RequestBody GoogleSubscriptionGetRequest request,
        HttpServletRequest servletRequest
    ) throws IOException {
        val data = purchaseService.verifyGoogleSubscriptionTransaction(user.getId(), request);
        final Purchase purchase = purchaseService.getByTransactionId(request.orderId());

        slackService.sendPurchaseMessage(SlackChannel.PURCHASE, servletRequest, purchase);
        return BaseResponse.success(GOOGLE_PURCHASE_SUBSCRIPTION_VERIFY_SUCCESS, data);
    }

    @PostMapping("/google/verify/ticket")
    public BaseResponse<GoogleTicketGetResponse> verifyGoogleTicketTransaction(
        @AccessTokenUser User user,
        @RequestBody GoogleTicketGetRequest request,
        HttpServletRequest servletRequest
    ) throws IOException {
        val data = purchaseService.verifyGoogleTicketTransaction(user.getId(), request);
        final Purchase purchase = purchaseService.getByTransactionId(request.orderId());

        slackService.sendPurchaseMessage(SlackChannel.PURCHASE, servletRequest, purchase);
        return BaseResponse.success(GOOGLE_PURCHASE_INAPP_VERIFY_SUCCESS, data);
    }

    @GetMapping("/subscribe")
    public BaseResponse<UserSubscribeNeededResponse> getUserSubscribeNeeded(
        @AccessTokenUser User user) {
        val data = purchaseService.getUserSubscribe(user, LocalDateTime.now());
        return BaseResponse.success(USER_SUBSCRIBE_NEEDED_READ_SUCCESS, data);
    }

    @DeleteMapping("/apple/refund")
    public BaseResponse refundInAppApple(
        @AccessTokenUser User user,
        AppleInAppRefundRequest request
    ) {
        purchaseService.refundInAppApple(user.getId(), request);
        return BaseResponse.success(VERIFY_RECEIPT_SUCCESS);
    }

    @GetMapping()
    public BaseResponse<UserPurchaseInfoResponse> getUserPurchaseInfo(@AccessTokenUser User user) {
        val data = UserPurchaseInfoResponse.of(user);
        return BaseResponse.success(USER_PURCHASE_INFO_READ_SUCCESS, data);
    }

}
