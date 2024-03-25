package com.yello.server.infrastructure.slack.configuration;

import com.yello.server.infrastructure.slack.dto.response.SlackChannel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfiguration {

    private static final String slackUrl = "https://hooks.slack.com/services";
    @Value("${slack.token.ambulence}")
    private String slackTokenForError;
    @Value("${slack.token.bank}")
    private String slackTokenForPurchase;
    @Value("${slack.token.sign-up}")
    private String slackTokenForSignUp;
    @Value("${slack.token.apple-bank-alarm}")
    private String slackTokenForApplePurchaseNotification;
    @Value("${slack.token.data-daily}")
    private String slackTokenDataDaily;

    @PostConstruct
    public void inject() {
        for (SlackChannel channel : SlackChannel.values()) {
            switch (channel) {
                case ERROR -> {
                    channel.SLACK_WEBHOOK_URL(String.format("%s/%s", slackUrl, slackTokenForError));
                }
                case PURCHASE -> {
                    channel.SLACK_WEBHOOK_URL(String.format("%s/%s", slackUrl, slackTokenForPurchase));
                }
                case SIGN_UP -> {
                    channel.SLACK_WEBHOOK_URL(String.format("%s/%s", slackUrl, slackTokenForSignUp));
                }
                case APPLE_PURCHASE_NOTIFICATION -> {
                    channel.SLACK_WEBHOOK_URL(String.format("%s/%s", slackUrl, slackTokenForApplePurchaseNotification));
                }
                case DATA_DAILY -> {
                    channel.SLACK_WEBHOOK_URL(String.format("%s/%s", slackUrl, slackTokenDataDaily));
                }
                default -> throw new IllegalStateException("Unexpected value: " + this);
            }
        }
    }
}
