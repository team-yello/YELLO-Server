package com.yello.server.infrastructure.scheduler;


import com.yello.server.infrastructure.batch.JobConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventScheduler {

    private final JobLauncher jobLauncher;
    private final JobConfiguration jobConfiguration;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Scheduled(cron = "0 0 12 * * ?")
    public void lunchEventRunJob() {

        //JobParamter의 역할은 반복해서 실행되는 Job의 유일한 ID임, 동일한 값이 세팅되면 두번째부터 실행안됨)
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("uuid", UUID.randomUUID().toString())
                .toJobParameters();
        System.out.println("hye!!!!!!!!!!!!");

        try {
            jobLauncher.run(jobConfiguration.lunchEventJob(jobRepository, transactionManager), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException | org.springframework.batch.core.repository.JobRestartException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
