package com.yello.server.infrastructure.slack.aspect;

import com.yello.server.infrastructure.slack.factory.SlackWebhookMessageFactory;
import jakarta.servlet.http.HttpServletRequest;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class SlackPurchaseNotificationAspect {

    private final SlackApi slackPurchaseApi;
    private final SlackWebhookMessageFactory slackWebhookMessageFactory;
    private final TaskExecutor taskExecutor;

    public SlackPurchaseNotificationAspect(
        @Qualifier("slackPurchaseApi") SlackApi slackPurchaseApi,
        SlackWebhookMessageFactory slackWebhookMessageFactory,
        TaskExecutor taskExecutor) {
        this.slackPurchaseApi = slackPurchaseApi;
        this.slackWebhookMessageFactory = slackWebhookMessageFactory;
        this.taskExecutor = taskExecutor;
    }

    @Around("@annotation(com.yello.server.infrastructure.slack.annotation.SlackPurchaseNotification)")
    Object purchaseNotification(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
            .getRequest();

        SlackMessage slackMessage = slackWebhookMessageFactory.generateSlackPurchaseMessage(
            request
        );

        Runnable runnable = () -> slackPurchaseApi.call(slackMessage);
        taskExecutor.execute(runnable);

        return proceedingJoinPoint.proceed();
    }
}
