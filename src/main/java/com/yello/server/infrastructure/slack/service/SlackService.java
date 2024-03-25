package com.yello.server.infrastructure.slack.service;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import com.yello.server.domain.purchase.dto.request.google.DeveloperNotification;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.infrastructure.slack.dto.response.SlackChannel;
import com.yello.server.infrastructure.slack.factory.SlackWebhookMessageFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackService {

    private final SlackWebhookMessageFactory messageFactory;
    private final Slack slack = Slack.getInstance();

    @Async
    public void sendErrorMessage(SlackChannel slackChannel, HttpServletRequest request, Exception exception)
        throws IOException {
        send(slackChannel, messageFactory.generateErrorPayload(request, exception));
    }

    @Async
    public void sendAppleStateChangedMessage(SlackChannel slackChannel, HttpServletRequest request)
        throws IOException, URISyntaxException {
        send(slackChannel, messageFactory.generateAppleStateChangedMessage(request));
    }

    @Async
    public void sendGoogleStateChangedMessage(SlackChannel slackChannel, HttpServletRequest request,
        Map.Entry<DeveloperNotification, Optional<Purchase>> notification)
        throws IOException, URISyntaxException {
        send(slackChannel, messageFactory.generateGoogleStateChangedMessage(request, notification));
    }

    @Async
    public void sendPurchaseMessage(SlackChannel slackChannel, HttpServletRequest request, Purchase purchase)
        throws IOException {
        send(slackChannel, messageFactory.generatePurchasePayload(request, purchase));
    }

    @Async
    public void sendSignUpMessage(SlackChannel slackChannel, HttpServletRequest request) throws IOException {
        send(slackChannel, messageFactory.generateSignUpPayload(request));
    }

    @Async
    public void send(SlackChannel channel, com.slack.api.webhook.Payload payload) throws IOException {
        final WebhookResponse response = slack.send(channel.SLACK_WEBHOOK_URL(), payload);
        log.info(response.toString());
    }
}
