package com.yello.server.infrastructure.batch;

import com.yello.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StepConfiguration {

    private final ChunkReader chunkReader;
    private final ChunkProcessor chunkProcessor;
    private final ChunkWriter chunkWriter;

    @Bean
    public Step lunchEventAlarmStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager) throws Exception {
        return new StepBuilder("lunchEventStep", jobRepository)
            .<User, User>chunk(10, transactionManager)
            .reader(chunkReader.userDataJdbcPagingItemReader())
            .processor(chunkProcessor.lunchEventProcessor())
            .writer(chunkWriter.lunchEventWriter())
            .build();
    }
}
