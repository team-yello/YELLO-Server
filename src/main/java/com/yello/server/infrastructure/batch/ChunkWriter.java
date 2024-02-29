package com.yello.server.infrastructure.batch;

import com.yello.server.domain.user.entity.User;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@Configuration
public class ChunkWriter {
    private final NotificationService notificationService;

    @Bean
    @StepScope
    public ItemWriter<User> lunchEventWriter() {
        return items -> items.forEach(notificationService::sendLunchEventNotification);
    }
}
