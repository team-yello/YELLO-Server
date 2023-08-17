package com.yello.server.infrastructure.slack.configuration;

import net.gpedro.integrations.slack.SlackApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfiguration {

    @Value("${slack.token}")
    String slackToken;

    @Bean
    SlackApi slackApi() {
        return new SlackApi("https://hooks.slack.com/services/" + slackToken);
    }
}
