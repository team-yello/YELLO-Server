package com.yello.server.global.common.util;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.purchase.dto.apple.AppleVerifyReceipt;
import com.yello.server.domain.purchase.dto.apple.AppleVerifyReceiptResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AppleUtil {

    private final ObjectMapper objectMapper;
    private Logger logger = LoggerFactory.getLogger(AppleUtil.class);
    @Value("${apple.production.uri}")
    private String APPLE_PRODUCTION_URL;
    @Value("${apple.sandbox.uri}")
    private String APPLE_SANDBOX_URL;
    @Value("${apple.password}")
    private String PASSWORD;

    public AppleVerifyReceiptResponse appleVerifyReceipt(AppleVerifyReceipt appleVerifyReceipt) {

        WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build();

        AppleVerifyReceiptResponse receiptResponse = webClient.post()
            .uri(APPLE_PRODUCTION_URL)
            .bodyValue(appleVerifyReceipt)
            .retrieve()
            .bodyToMono(AppleVerifyReceiptResponse.class)
            .block();

        int statusCode = receiptResponse.status();
        if (statusCode == 21007) {
            receiptResponse = webClient.post()
                .uri(APPLE_SANDBOX_URL)
                .body(appleVerifyReceipt, AppleVerifyReceipt.class)
                .retrieve()
                .bodyToMono(AppleVerifyReceiptResponse.class)
                .block();
        } else if (statusCode != 0) {
            verifyStatusCode(statusCode);
        }

        return receiptResponse;
    }

    private void verifyStatusCode(int statusCode) {
        switch (statusCode) {
            case 21000:
                logger.error("[Status code: " + statusCode
                    + "] The request to the App Store was not made using the HTTP POST request method.");
                break;
            case 21001:
                logger.error("[Status code: " + statusCode
                    + "] This status code is no longer sent by the App Store.");
                break;
            case 21002:
                logger.error("[Status code: " + statusCode
                    + "] The data in the receipt-data property was malformed or the service experienced a temporary issue. Try again.");
                break;
            case 21003:
                logger.error(
                    "[Status code: " + statusCode + "] The receipt could not be authenticated.");
                break;
            case 21004:
                logger.error("[Status code: " + statusCode
                    + "] The shared secret you provided does not match the shared secret on file for your account.");
                break;
            case 21005:
                logger.error("[Status code: " + statusCode
                    + "] The receipt server was temporarily unable to provide the receipt. Try again.");
                break;
            case 21006:
                logger.error("[Status code: " + statusCode
                    + "] This receipt is valid but the subscription has expired. When this status code is returned to your server, the receipt data is also decoded and returned as part of the response. Only returned for iOS 6-style transaction receipts for auto-renewable subscriptions.");
                break;
            case 21008:
                logger.error("[Status code: " + statusCode
                    + "] This receipt is from the production environment, but it was sent to the test environment for verification.");
                break;
            case 21009:
                logger.error("[Status code: " + statusCode
                    + "] Internal data access error. Try again later.");
                break;
            case 21010:
                logger.error("[Status code: " + statusCode
                    + "] The user account cannot be found or has been deleted.");
                break;
            default:
                logger.error("[Status code: " + statusCode
                    + "] The receipt for the App Store is incorrect.");
                break;
        }

        throw new IllegalStateException(
            "[verifyReceipt] The receipt for the App Store is incorrect.");
    }

}
