package com.yello.server.infrastructure.slack.configuration;

import net.gpedro.integrations.slack.SlackApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfiguration {

    @Value("${slack.token.ambulence}")
    String slackTokenAmbulence;

    @Value("${slack.token.bank}")
    String slackTokenBank;

    @Bean
    SlackApi slackAmbulenceApi() {
        return new SlackApi("https://hooks.slack.com/services/" + slackTokenAmbulence);
    }

    @Bean
    SlackApi slackBankApi() {
        return new SlackApi("https://hooks.slack.com/services/" + slackTokenBank);
    }
}
