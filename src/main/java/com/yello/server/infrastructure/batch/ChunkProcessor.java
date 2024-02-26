package com.yello.server.infrastructure.batch;


import com.yello.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ChunkProcessor {

    @Bean
    @StepScope
    public ItemProcessor<User, User> lunchEventProcessor() {
        return user -> new User);
    }
}
