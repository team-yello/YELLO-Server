package com.yello.server.infrastructure.slack.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.authorization.service.TokenProvider;
import com.yello.server.domain.purchase.service.PurchaseManager;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.factory.TimeFactory;
import com.yello.server.infrastructure.slack.dto.response.SlackAppleNotificationResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlackWebhookMessageFactory {

    private static final String ERROR_TITLE = "긴급 환자가 이송되었습니다";
    private static final String ERROR_USERNAME = "옐로 소방서";
    private static final String PURCHASE_TITLE = "돈이 입금되었습니다.";
    private static final String PURCHASE_USERNAME = "옐로 뱅크";

    private static final String SIGNUP_TITLE = "신규 유저가 가입하였습니다. ";
    private static final String SIGNUP_USERNAME = "옐로 온보딩";

    private static final String APPLE_PURCHASE_ALARM_TITLE = "애플이 결제 관련 알림을 보냈습니다.";
    private static final String APPLE_PURCHASE_ALARM_USERNAME = "애플 뱅크 알림";
    private final PurchaseManager purchaseManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    private static String getRequestBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    public SlackMessage generateSlackErrorMessage(
        HttpServletRequest request,
        Exception exception
    ) throws IOException {
        return new SlackMessage()
            .setAttachments(generateSlackErrorAttachment(request, exception))
            .setText(ERROR_TITLE)
            .setUsername(ERROR_USERNAME);
    }

    public SlackMessage generateSlackPurchaseMessage(
        HttpServletRequest request
    ) throws IOException {
        return new SlackMessage()
            .setAttachments(generateSlackPurchaseAttachment(request))
            .setText(PURCHASE_TITLE)
            .setUsername(PURCHASE_USERNAME);
    }

    public SlackMessage generateSlackSignUpMessage(
        HttpServletRequest request
    ) throws IOException {
        return new SlackMessage()
            .setAttachments(generateSlackSignUpAttachment(request))
            .setText(SIGNUP_TITLE)
            .setUsername(SIGNUP_USERNAME);
    }

    public SlackMessage generateAppleSlackPurchaseMessage(
        HttpServletRequest request
    ) throws IOException {
        return new SlackMessage()
            .setAttachments(generateSlackApplePurchaseAttachment(request))
            .setText(APPLE_PURCHASE_ALARM_TITLE)
            .setUsername(APPLE_PURCHASE_ALARM_USERNAME);
    }

    private List<SlackAttachment> generateSlackErrorAttachment(
        HttpServletRequest request,
        Exception exception
    ) throws IOException {
        final SlackAttachment slackAttachment = new SlackAttachment()
            .setFallback("Error")
            .setColor("danger")
            .setTitle(ERROR_TITLE)
            .setTitleLink(request.getContextPath())
            .setText("Exception Class : " + exception.getClass().getName() + "\n"
                + "Exception Message : " + exception.getMessage() + "\n"
                + Arrays.toString(exception.getStackTrace()))
            .setColor("danger")
            .setFields(generateSlackFieldList(request));
        return Collections.singletonList(slackAttachment);
    }

    private List<SlackAttachment> generateSlackPurchaseAttachment(
        HttpServletRequest request
    ) throws IOException {
        final SlackAttachment slackAttachment = new SlackAttachment()
            .setFallback("good")
            .setColor("good")
            .setTitle(PURCHASE_TITLE)
            .setTitleLink(request.getContextPath())
            .setText(PURCHASE_TITLE)
            .setFields(generateSlackFieldList(request));
        return Collections.singletonList(slackAttachment);
    }

    private List<SlackAttachment> generateSlackSignUpAttachment(
        HttpServletRequest request
    ) throws IOException {
        final SlackAttachment slackAttachment = new SlackAttachment()
            .setFallback("good")
            .setColor("good")
            .setTitle(SIGNUP_TITLE)
            .setTitleLink(request.getContextPath())
            .setText(SIGNUP_TITLE)
            .setFields(generateSlackSimpleFieldList(request));
        return Collections.singletonList(slackAttachment);
    }

    private List<SlackAttachment> generateSlackApplePurchaseAttachment(
        HttpServletRequest request
    ) throws IOException {
        final SlackAttachment slackAttachment = new SlackAttachment()
            .setFallback("good")
            .setColor("good")
            .setTitle(APPLE_PURCHASE_ALARM_TITLE)
            .setTitleLink(request.getContextPath())
            .setText(APPLE_PURCHASE_ALARM_TITLE)
            .setFields(generateSlackAppleFieldList(request));
        return Collections.singletonList(slackAttachment);
    }

    private List<SlackField> generateSlackFieldList(
        HttpServletRequest request
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token = authHeader == null ? "null" : authHeader.substring("Bearer ".length());
        final Long userId = authHeader == null ? -1L : tokenProvider.getUserId(token);
        final Optional<User> user =
            authHeader == null ? Optional.empty() : userRepository.findById(userId);
        final String yelloId = user.isPresent() ? user.get().getYelloId() : "null";
        final String deviceToken = user.isPresent() ? user.get().getDeviceToken() : "null";

        String userInfo =
            String.format("userId : %d %nyelloId : %s %ndeviceToken : %s", userId, yelloId,
                deviceToken);

        return Arrays.asList(
            new SlackField().setTitle("Request Method").setValue(request.getMethod()),
            new SlackField().setTitle("Request URL").setValue(request.getRequestURL().toString()),
            new SlackField().setTitle("Request Time")
                .setValue(TimeFactory.toDateFormattedString(LocalDateTime.now())),
            new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr()),
            new SlackField().setTitle("Request Headers")
                .setValue(request.toString()),
            new SlackField().setTitle("Request Body")
                .setValue(getRequestBody(request)),
            new SlackField().setTitle("인증/인가 정보 - Authorization")
                .setValue(request.getHeader(HttpHeaders.AUTHORIZATION)),
            new SlackField().setTitle("인증/인가 정보 - 유저").setValue(userInfo)
        );
    }

    private List<SlackField> generateSlackSimpleFieldList(
        HttpServletRequest request
    ) throws IOException {
        return Arrays.asList(
            new SlackField().setTitle("Request Body")
                .setValue(getRequestBody(request))
        );
    }

    private List<SlackField> generateSlackAppleFieldList(
        HttpServletRequest request
    ) throws IOException {
        return Arrays.asList(
            new SlackField().setTitle("Request Method").setValue(request.getMethod()),
            new SlackField().setTitle("Request URL").setValue(request.getRequestURL().toString()),
            new SlackField().setTitle("Request Time")
                .setValue(TimeFactory.toDateFormattedString(LocalDateTime.now())),
            new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr()),
            new SlackField().setTitle("Request Headers")
                .setValue(request.toString()),
            new SlackField().setTitle("Request Body")
                .setValue(generateSlackAppleNotificationRequestField(request))
        );
    }

    private String generateSlackAppleNotificationRequestField(
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

        SlackAppleNotificationResponse response =
            purchaseManager.checkPurchaseDataByAppleSignedPayload(payload[0]);

        return response.toString();
    }
}
