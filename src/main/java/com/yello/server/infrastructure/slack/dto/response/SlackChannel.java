package com.yello.server.infrastructure.slack.dto.response;

/**
 * @see com.yello.server.infrastructure.slack.configuration.SlackConfiguration
 */
public enum SlackChannel {
    ERROR,
    PURCHASE,
    SIGN_UP,
    @Deprecated(since = "2.0.3v")
    APPLE_PURCHASE_NOTIFICATION,
    DATA_DAILY;

    private String SLACK_WEBHOOK_URL;

    public String SLACK_WEBHOOK_URL() {
        return SLACK_WEBHOOK_URL;
    }

    public void SLACK_WEBHOOK_URL(String SLACK_WEBHOOK_URL) {
        this.SLACK_WEBHOOK_URL = SLACK_WEBHOOK_URL;
    }
}
