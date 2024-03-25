package com.yello.server.infrastructure.slack.factory;

import static com.yello.server.global.common.util.RestUtil.getRequestBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.model.Attachment;
import com.slack.api.model.block.ActionsBlock;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.UnknownBlock;
import com.slack.api.model.block.UnknownBlockElement;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.ButtonElement;
import com.slack.api.model.block.element.ImageElement;
import com.slack.api.webhook.Payload;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.repository.UserGroupRepository;
import com.yello.server.domain.purchase.dto.request.google.DeveloperNotification;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.service.PurchaseManager;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.factory.TimeFactory;
import com.yello.server.global.common.util.ConstantUtil;
import com.yello.server.infrastructure.slack.dto.response.SlackAppleNotificationResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlackWebhookMessageFactory {

    /**
     * Error
     */
    private static final String ERROR_TITLE = "500에러가 발생하였습니다.";
    private static final String ERROR_SECTION_0_TEXT = "• *Exception Class*: %s\n• *Exception Message*: %s";
    private static final String ERROR_BUTTON_0_TITLE = "통신 내역 자세히 보기";

    /**
     * Purchase
     */
    private static final String PURCHASE_TITLE = "결제가 체결되었습니다.";
    private static final String PURCHASE_SECTION_0_TEXT = "• *이름*: %s\n• *성별*: %s\n• *yelloId*: %s\n• *그룹*: %s";
    private static final String PURCHASE_IMAGE_ALT = "profile_image";
    private static final String PURCHASE_SECTION_1_TEXT = "• *결제 종류*: %s\n• *플랫폼*: %s\n• *금액*: %s";
    private static final String PURCHASE_BUTTON_0_TITLE = "유저 정보 확인하기";
    private static final String PURCHASE_BUTTON_1_TITLE = "통신 내역 자세히 보기";

    /**
     * SignUp
     */
    private static final String SIGNUP_TITLE = "신규 회원이 회원가입하였습니다.";
    private static final String SIGNUP_SECTION_TEXT = "• *이름*: %s\n• *성별*: %s\n• *yelloId*: %s\n• *그룹*: %s\n• *친구추가*: %s명\n• *추천인*: %s";
    private static final String SIGNUP_IMAGE_ALT = "profile_image";
    private static final String SIGNUP_BUTTON_0_TITLE = "유저 정보 확인하기";
    private static final String SIGNUP_BUTTON_1_TITLE = "통신 내역 자세히 보기";

    /**
     * Apple State
     */
    private static final String APPLE_STATE_TITLE = "결제 상태가 변경되었습니다.";
    private static final String APPLE_STATE_SECTION_0_TEXT = "• *이름*: %s\n• *성별*: %s\n• *yelloId*: %s\n• *그룹*: %s";
    private static final String APPLE_STATE_IMAGE_ALT = "profile_image";
    private static final String APPLE_STATE_SECTION_1_TEXT = "• *영수증 ID*: %s\n• *notificationType*: %s\n• *subtype*: %s\n> %s";
    private static final String APPLE_STATE_BUTTON_0_TITLE = "유저 정보 확인하기";
    private static final String APPLE_STATE_BUTTON_1_TITLE = "통신 내역 자세히 보기";
    private static final String APPLE_STATE_BUTTON_2_TITLE = "관련 문서보기";
    private static final String APPLE_STATE_BUTTON_2_LINK = "https://developer.apple.com/documentation/appstoreservernotifications/responsebodyv2decodedpayload";

    /**
     * Google State
     */
    private static final String GOOGLE_STATE_TITLE = "결제 상태가 변경되었습니다.";
    private static final String GOOGLE_STATE_SECTION_0_TEXT = "• *이름*: %s\n• *성별*: %s\n• *yelloId*: %s\n• *그룹*: %s";
    private static final String GOOGLE_STATE_IMAGE_ALT = "profile_image";
    private static final String GOOGLE_STATE_SECTION_1_TEXT = "• *영수증 ID*: %s\n• *결제 종류*: %s\n• *Type*: %s\n> %s";
    private static final String GOOGLE_STATE_BUTTON_0_TITLE = "유저 정보 확인하기";
    private static final String GOOGLE_STATE_BUTTON_1_TITLE = "통신 내역 자세히 보기";
    private static final String GOOGLE_STATE_BUTTON_2_TITLE = "관련 문서보기";
    private static final String GOOGLE_STATE_BUTTON_2_LINK = "https://developer.android.com/google/play/billing/rtdn-reference";

    /**
     * Unicode
     */
    private static final String lineBreak = "%0A";
    private static final String space = "%20";
    private static final String doubleQuote = "%22";
    private static final String singleQuote = "%27";
    private static final String lparen = "%7B";
    private static final String rparen = "%7D";
    private static final String lbracket = "%5B";
    private static final String rbracket = "%5D";

    private final ObjectMapper objectMapper;
    private final PurchaseManager purchaseManager;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;

    @Value("${web.url.yelloworld}")
    private String yelloworldUrl;

    public com.slack.api.webhook.Payload generateErrorPayload(HttpServletRequest request, Exception exception)
        throws IOException {
        URI button0URI;
        try {
            button0URI = generateRequestUri(String.format("%s%s", yelloworldUrl, "/admin/log/http"), request);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        return Payload.builder()
            .attachments(List.of(
                Attachment.builder()
                    .text(Arrays.toString(exception.getStackTrace()))
                    .build()
            ))
            .blocks(List.of(
                HeaderBlock.builder()
                    .text(PlainTextObject.builder()
                        .text(ERROR_TITLE)
                        .build())
                    .build(),
                DividerBlock.builder().build(),
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text(String.format(ERROR_SECTION_0_TEXT,
                            exception.getClass().getName(),
                            exception.getMessage()
                        ))
                        .build())
                    .build(),
                DividerBlock.builder().build(),
                ActionsBlock.builder()
                    .elements(List.of(
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(ERROR_BUTTON_0_TITLE)
                                .build())
                            .url(button0URI.toASCIIString())
                            .build()
                    ))
                    .build()
            ))
            .build();
    }

    public com.slack.api.webhook.Payload generatePurchasePayload(HttpServletRequest request, Purchase purchase)
        throws IOException {
        final User user = purchase.getUser();

        URI button0URI;
        URI button1URI;
        try {
            button0URI = new URI(String.format("%s%s%s", yelloworldUrl, "/admin/user/", user.getId()));
            button1URI = generateRequestUri(String.format("%s%s", yelloworldUrl, "/admin/log/http"), request);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        return Payload.builder()
            .blocks(List.of(
                HeaderBlock.builder()
                    .text(PlainTextObject.builder()
                        .text(PURCHASE_TITLE)
                        .build())
                    .build(),
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text(String.format(PURCHASE_SECTION_0_TEXT,
                            user.getName(),
                            user.getGender(),
                            user.getYelloId(),
                            user.getGroup().toString()
                        ))
                        .build())
                    .accessory(ImageElement.builder()
                        .imageUrl(user.getProfileImage())
                        .altText(PURCHASE_IMAGE_ALT)
                        .build())
                    .build(),
                DividerBlock.builder().build(),
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text(String.format(PURCHASE_SECTION_1_TEXT,
                            purchase.getProductType(),
                            purchase.getGateway(),
                            purchase.getPrice()
                        ))
                        .build())
                    .build(),
                DividerBlock.builder().build(),
                ActionsBlock.builder()
                    .elements(List.of(
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(PURCHASE_BUTTON_0_TITLE)
                                .build())
                            .url(button0URI.toASCIIString())
                            .build(),
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(PURCHASE_BUTTON_1_TITLE)
                                .build())
                            .url(button1URI.toASCIIString())
                            .build()
                    ))
                    .build()
            ))
            .build();
    }

    public com.slack.api.webhook.Payload generateSignUpPayload(HttpServletRequest request) throws IOException {
        final String requestBody = getRequestBody(request);
        final SignUpRequest signUpRequest = objectMapper.readValue(requestBody, SignUpRequest.class);
        final UserGroup userGroup = userGroupRepository.getById(signUpRequest.groupId());
        final User user = userRepository.getByYelloId(signUpRequest.yelloId());

        URI button0URI;
        URI button1URI;
        try {
            button0URI = new URI(String.format("%s%s%s", yelloworldUrl, "/admin/user/", user.getId()));
            button1URI = generateRequestUri(String.format("%s%s", yelloworldUrl, "/admin/log/http"), request);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        return Payload.builder()
            .blocks(List.of(
                HeaderBlock.builder()
                    .text(PlainTextObject.builder()
                        .text(SIGNUP_TITLE)
                        .build())
                    .build(),
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text(String.format(SIGNUP_SECTION_TEXT,
                            signUpRequest.name(),
                            signUpRequest.gender(),
                            signUpRequest.yelloId(),
                            userGroup.toString(),
                            signUpRequest.friends().size(),
                            signUpRequest.recommendId()
                        ))
                        .build())
                    .accessory(ImageElement.builder()
                        .imageUrl(signUpRequest.profileImage())
                        .altText(SIGNUP_IMAGE_ALT)
                        .build())
                    .build(),
                DividerBlock.builder().build(),
                ActionsBlock.builder()
                    .elements(List.of(
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(SIGNUP_BUTTON_0_TITLE)
                                .build())
                            .url(button0URI.toASCIIString())
                            .build(),
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(SIGNUP_BUTTON_1_TITLE)
                                .build())
                            .url(button1URI.toASCIIString())
                            .build()
                    ))
                    .build()
            ))
            .build();
    }

    public com.slack.api.webhook.Payload generateAppleStateChangedMessage(HttpServletRequest request)
        throws IOException, URISyntaxException {
        final SlackAppleNotificationResponse notificationResponse = decodeAppleNotificationRequestField(
            request);
        final User user = userRepository.getById(notificationResponse.userId());

        URI button0URI;
        URI button1URI;
        final URI button2URI = new URI(APPLE_STATE_BUTTON_2_LINK);
        try {
            button0URI = new URI(String.format("%s%s%s", yelloworldUrl, "/admin/user/", user.getId()));
            button1URI = generateRequestUri(String.format("%s%s", yelloworldUrl, "/admin/log/http"), request);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        return Payload.builder()
            .blocks(List.of(
                HeaderBlock.builder()
                    .text(PlainTextObject.builder()
                        .text(APPLE_STATE_TITLE)
                        .build())
                    .build(),
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text(String.format(APPLE_STATE_SECTION_0_TEXT,
                            user.getName(),
                            user.getGender(),
                            user.getYelloId(),
                            user.getGroup().toString()
                        ))
                        .build())
                    .accessory(ImageElement.builder()
                        .imageUrl(user.getProfileImage())
                        .altText(APPLE_STATE_IMAGE_ALT)
                        .build())
                    .build(),
                DividerBlock.builder().build(),
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text(String.format(APPLE_STATE_SECTION_1_TEXT,
                            notificationResponse.transactionId(),
                            notificationResponse.notificationType(),
                            notificationResponse.subtype(),
                            getAppleStateLabel(notificationResponse.notificationType(), notificationResponse.subtype())
                        ))
                        .build())
                    .build(),
                DividerBlock.builder().build(),
                ActionsBlock.builder()
                    .elements(List.of(
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(APPLE_STATE_BUTTON_0_TITLE)
                                .build())
                            .url(button0URI.toASCIIString())
                            .build(),
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(APPLE_STATE_BUTTON_1_TITLE)
                                .build())
                            .url(button1URI.toASCIIString())
                            .build(),
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(APPLE_STATE_BUTTON_2_TITLE)
                                .build())
                            .url(button2URI.toASCIIString())
                            .build()
                    ))
                    .build()
            ))
            .build();
    }

    public com.slack.api.webhook.Payload generateGoogleStateChangedMessage(HttpServletRequest request,
        Map.Entry<DeveloperNotification, Optional<Purchase>> notification)
        throws IOException, URISyntaxException {
        final DeveloperNotification developerNotification = notification.getKey();
        final ProductType productType = developerNotification.getProductType();
        final Optional<Purchase> purchase = notification.getValue();

        URI button0URI;
        URI button1URI;
        final URI button2URI = new URI(GOOGLE_STATE_BUTTON_2_LINK);
        try {
            button0URI = productType == ProductType.TEST
                ? new URI(yelloworldUrl)
                : new URI(String.format("%s%s%s", yelloworldUrl, "/admin/user/", purchase.get().getUser().getId()));
            button1URI = generateRequestUri(String.format("%s%s", yelloworldUrl, "/admin/log/http"), request);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        return Payload.builder()
            .blocks(List.of(
                HeaderBlock.builder()
                    .text(PlainTextObject.builder()
                        .text(GOOGLE_STATE_TITLE)
                        .build())
                    .build(),
                (
                    productType == ProductType.TEST ?
                        UnknownBlock.builder().build() :
                        SectionBlock.builder()
                            .text(MarkdownTextObject.builder()
                                .text(String.format(GOOGLE_STATE_SECTION_0_TEXT,
                                    purchase.get().getUser().getName(),
                                    purchase.get().getUser().getGender(),
                                    purchase.get().getUser().getYelloId(),
                                    purchase.get().getUser().getGroup().toString()
                                ))
                                .build())
                            .accessory(ImageElement.builder()
                                .imageUrl(purchase.get().getUser().getProfileImage())
                                .altText(GOOGLE_STATE_IMAGE_ALT)
                                .build())
                            .build()
                ),
                (
                    productType == ProductType.TEST ?
                        UnknownBlock.builder().build() :
                        DividerBlock.builder().build()
                ),
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text(String.format(GOOGLE_STATE_SECTION_1_TEXT,
                            purchase.map(Purchase::getTransactionId).orElse(null),
                            productType,
                            getGoogleStateTypeLabel(developerNotification),
                            getGoogleStateLabel(developerNotification)
                        ))
                        .build())
                    .build(),
                DividerBlock.builder().build(),
                ActionsBlock.builder()
                    .elements(List.of(
                        (
                            productType == ProductType.TEST ?
                                UnknownBlockElement.builder().build() :
                                ButtonElement.builder()
                                    .text(PlainTextObject.builder()
                                        .text(GOOGLE_STATE_BUTTON_0_TITLE)
                                        .build())
                                    .url(button0URI.toASCIIString())
                                    .build()
                        ),
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(GOOGLE_STATE_BUTTON_1_TITLE)
                                .build())
                            .url(button1URI.toASCIIString())
                            .build(),
                        ButtonElement.builder()
                            .text(PlainTextObject.builder()
                                .text(GOOGLE_STATE_BUTTON_2_TITLE)
                                .build())
                            .url(button2URI.toASCIIString())
                            .build()
                    ))
                    .build()
            ))
            .build();
    }

    private URI generateRequestUri(String link, HttpServletRequest request) throws IOException, URISyntaxException {
        // Headers
        StringBuilder headersBuilder = new StringBuilder();
        request.getHeaderNames().asIterator()
            .forEachRemaining(headerName -> headersBuilder
                .append(headerName)
                .append(": ")
                .append(request.getHeader(headerName))
                .append("\n")
            );

        Map<String, String> button1Parameters = new HashMap<>();
        button1Parameters.put("method", request.getMethod());
        button1Parameters.put("url", request.getRequestURL().toString());
        button1Parameters.put("time",
            TimeFactory.toDateFormattedString(ZonedDateTime.now(ConstantUtil.GlobalZoneId).toLocalDateTime()));
        button1Parameters.put("ip", request.getRemoteAddr());
        button1Parameters.put("headers", headersBuilder.toString());
        button1Parameters.put("request_body", getRequestBody(request));

        StringBuilder stringBuilder = new StringBuilder(link);

        // Queries
        stringBuilder.append("?");
        for (Map.Entry<String, String> entry : button1Parameters.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
            stringBuilder.append("&");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        // replace to unicode
        return new URI(stringBuilder.toString()
            .replace(" ", space)
            .replace("\n", lineBreak)
            .replace("{", lparen)
            .replace("}", rparen)
            .replace("[", lbracket)
            .replace("]", rbracket)
            .replace("'", singleQuote)
            .replace("\"", doubleQuote)
        );
    }

    private SlackAppleNotificationResponse decodeAppleNotificationRequestField(
        HttpServletRequest request
    ) throws IOException {
        String jsonString = getRequestBody(request);
        ObjectMapper objectMapper = new ObjectMapper();
        String[] payload = new String[1];
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            if (jsonNode.isObject()) {
                jsonNode.fields().forEachRemaining(entry -> {
                    payload[0] = entry.getValue().asText();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return purchaseManager.checkPurchaseDataByAppleSignedPayload(payload[0]);
    }

    private String getGoogleStateTypeLabel(DeveloperNotification developerNotification) {
        switch (Objects.requireNonNull(developerNotification.getProductType())) {
            case TEST -> {
                return ProductType.TEST.name();
            }
            case YELLO_PLUS -> {
                return developerNotification.subscriptionNotification().notificationType().name();
            }
            case ONE_TICKET, TWO_TICKET, FIVE_TICKET -> {
                return developerNotification.oneTimeProductNotification().notificationType().name();
            }
            default -> {
                return "";
            }
        }
    }

    private String getGoogleStateLabel(DeveloperNotification developerNotification) {
        switch (Objects.requireNonNull(developerNotification.getProductType())) {
            case TEST -> {
                return ProductType.TEST.name();
            }
            case YELLO_PLUS -> {
                return switch (developerNotification.subscriptionNotification().notificationType()) {
                    case SUBSCRIPTION_RECOVERED -> "정기 결제 복구";
                    case SUBSCRIPTION_RENEWED -> "정기 결제 갱신";
                    case SUBSCRIPTION_CANCELED -> "정기 결제 취소";
                    case SUBSCRIPTION_PURCHASED -> "첫 정기 결제";
                    case SUBSCRIPTION_ON_HOLD -> "정기 결제 보류";
                    case SUBSCRIPTION_IN_GRACE_PERIOD -> "정기 결제 유예";
                    case SUBSCRIPTION_RESTARTED -> "정기 결제 복원";
                    case SUBSCRIPTION_PRICE_CHANGE_CONFIRMED -> "가격 변경 확인 완료";
                    case SUBSCRIPTION_DEFERRED -> "구독 갱신 연장";
                    case SUBSCRIPTION_PAUSED -> "구독 일시중지";
                    case SUBSCRIPTION_PAUSE_SCHEDULE_CHANGED -> "구독 일시중지 일정 변경";
                    case SUBSCRIPTION_REVOKED -> "만료 시간 전, 정기 결제 취소";
                    case SUBSCRIPTION_EXPIRED -> "정기 결제 만료";
                };
            }
            case ONE_TICKET, TWO_TICKET, FIVE_TICKET -> {
                return switch (developerNotification.oneTimeProductNotification().notificationType()) {
                    case ONE_TIME_PRODUCT_PURCHASED -> "일회성 제품 구매";
                    case ONE_TIME_PRODUCT_CANCELED -> "일회성 제품 구매 취소";
                };
            }
            default -> {
                return "";
            }
        }
    }

    private String getAppleStateLabel(String notificationType, String subType) {
        switch (notificationType) {
            case "SUBSCRIBED" -> {
                return switch (subType) {
                    case "INITIAL_BUY" -> "그룹 첫 구독";
                    case "RESUBSCRIBE" -> "그룹 재 구독";

                    default -> "";
                };
            }
            case "DID_RENEW" -> {
                return switch (subType) {
                    case "" -> "구독 갱신";
                    case "BILLING_RECOVERY" -> "청구 재시도를 통해 구독 갱신";
                    default -> "";
                };
            }
            case "DID_CHANGE_RENEWAL_PREF" -> {
                return switch (subType) {
                    case "DOWNGRADE" -> "그룹 구독 다운그레이드 필요";
                    case "UPGRADE" -> "그룹 구독 업그레이드";
                    case "" -> "그룹 구독 다운그레이드 취소";
                    default -> "";
                };
            }
            case "DID_CHANGE_RENEWAL_STATUS" -> {
                return switch (subType) {
                    case "AUTO_RENEW_DISABLED" -> "구독 취소 또는 환불";
                    case "AUTO_RENEW_ENABLED" -> "재구독 및 자동 갱신 활성화";
                    case "" -> "가격 인상 고지 후 구독 취소";
                    default -> "";
                };
            }
            case "OFFER_REDEEMED" -> {
                return switch (subType) {
                    case "" -> "프로모션/쿠폰 코드 사용";
                    case "INITIAL_BUY" -> "코드 사용하여 첫 구독";
                    case "RESUBSCRIBE" -> "코드 사용하여 재 구독";
                    case "UPGRADE" -> "코드 사용하여 업그레이드";
                    case "DOWNGRADE" -> "코드 사용하여 다운그레이드";
                    default -> "";
                };
            }
            case "EXPIRED" -> {
                return switch (subType) {
                    case "VOLUNTARY", "BILLING_RETRY" -> "구독 만료";
                    case "PRODUCT_NOT_FOR_SALE" -> "판매자가 판매를 중지하여 구독 만료";
                    case "PRICE_INCREASE" -> "가격 인상 비동의로 구독 만료";
                    default -> "";
                };
            }
            case "DID_FAIL_TO_RENEW" -> {
                return switch (subType) {
                    case "", "GRACE_PERIOD" -> "갱신이 되지 않아 재시도 시간에 다시시도";
                    default -> "";
                };
            }
            case "GRACE_PERIOD_EXPIRED" -> {
                return "구독이 청구 유예 기간을 종료하고 청구 재시도를 계속합니다.";
            }
            case "PRICE_INCREASE" -> {
                return switch (subType) {
                    case "PENDING" -> "가격 인상 고지 확인 대기 중";
                    case "ACCEPTED" -> "가격 인상 동의";
                    default -> "";
                };
            }
            case "REFUND" -> {
                return "거래 환불";
            }
            case "REFUND_REVERSED" -> {
                return "환불 취소";
            }
            case "REFUND_DECLINED" -> {
                return "환불 거부";
            }
            case "CONSUMPTION_REQUEST" -> {
                return "환불 정보 요청";
            }
            case "REVOKE" -> {
                return "가족 내 엑세스 없음";
            }
            case "RENEWAL_EXTENDED" -> {
                return switch (subType) {
                    case "", "SUMMARY" -> "구독 갱신 날짜 연장 성공";
                    case "FAILURE" -> "구독 갱신 날짜 연장 실패";
                    default -> "";
                };
            }
            default -> {
                return "";
            }
        }
    }
}
