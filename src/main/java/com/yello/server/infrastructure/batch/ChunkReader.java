package com.yello.server.infrastructure.batch;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserJpaRepository;
import com.yello.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
public class ChunkReader {
    private final UserJpaRepository userRepository;

    @Bean
    @StepScope
    public RepositoryItemReader<User> userDataReader() {

        return new RepositoryItemReaderBuilder<User>()
                .name("userDataReader")
                .repository(userRepository)
                .methodName("findAllByPageable")
                .pageSize(100)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }
}
