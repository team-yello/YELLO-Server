package com.yello.server.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final StepConfiguration stepConfiguration;

    @Bean
    public Job lunchEventJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new JobBuilder("lunchEventJob", jobRepository)
                .start(stepConfiguration.lunchEventAlarmStep(jobRepository, transactionManager))
                .build();
    }


}
