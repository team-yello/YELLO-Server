package com.yello.server.infrastructure.batch;


import com.yello.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ChunkProcessor {
    public ItemProcessor<User, User> lunchEventProcessor() {
        ItemProcessor<User, User> item = user -> {
            System.out.println(user.getId() + ", "+ user.getName() + " dds121212");
            return user;
        };
        return item;
    }

}
