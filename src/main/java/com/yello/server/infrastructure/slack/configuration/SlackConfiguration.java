package com.yello.server.infrastructure.slack.configuration;

import net.gpedro.integrations.slack.SlackApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfiguration {

    @Value("${slack.token.ambulence}")
    String slackTokenForError;

    @Value("${slack.token.bank}")
    String slackTokenForPurchase;

    @Bean
    SlackApi slackErrorApi() {
        return new SlackApi("https://hooks.slack.com/services/" + slackTokenForError);
    }

    @Bean
    SlackApi slackPurchaseApi() {
        return new SlackApi("https://hooks.slack.com/services/" + slackTokenForPurchase);
    }
}
