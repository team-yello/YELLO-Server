package com.yello.server.global.common.util;

import net.gpedro.integrations.slack.SlackApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackUtil {

    @Value("${slack.token}")
    String slackToken;

    @Bean
    SlackApi slackApi() {
        System.out.println("slackToken = " + slackToken);
        return new SlackApi("https://hooks.slack.com/services/" + slackToken);
    }
}
