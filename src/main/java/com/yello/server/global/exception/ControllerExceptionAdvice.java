package com.yello.server.global.exception;

import static com.yello.server.global.common.ErrorCode.FIELD_REQUIRED_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.QUERY_STRING_REQUIRED_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.yello.server.domain.admin.exception.AdminConfigurationNotFoundException;
import com.yello.server.domain.admin.exception.UserAdminBadRequestException;
import com.yello.server.domain.admin.exception.UserAdminNotFoundException;
import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.authorization.exception.CustomAuthenticationException;
import com.yello.server.domain.authorization.exception.ExpiredTokenException;
import com.yello.server.domain.authorization.exception.InvalidTokenException;
import com.yello.server.domain.authorization.exception.NotExpiredTokenForbiddenException;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.authorization.exception.NotValidTokenForbiddenException;
import com.yello.server.domain.authorization.exception.OAuthException;
import com.yello.server.domain.event.exception.EventBadRequestException;
import com.yello.server.domain.event.exception.EventForbiddenException;
import com.yello.server.domain.event.exception.EventNotFoundException;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.exception.FriendNotFoundException;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.notice.exception.NoticeNotFoundException;
import com.yello.server.domain.purchase.exception.AppleBadRequestException;
import com.yello.server.domain.purchase.exception.GoogleBadRequestException;
import com.yello.server.domain.purchase.exception.GoogleTokenNotFoundException;
import com.yello.server.domain.purchase.exception.PurchaseConflictException;
import com.yello.server.domain.purchase.exception.PurchaseException;
import com.yello.server.domain.purchase.exception.PurchaseNotFoundException;
import com.yello.server.domain.purchase.exception.SubscriptionConflictException;
import com.yello.server.domain.question.exception.QuestionException;
import com.yello.server.domain.question.exception.QuestionNotFoundException;
import com.yello.server.domain.statistics.exception.StatisticsNotFoundException;
import com.yello.server.domain.user.exception.UserBadRequestException;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.exception.UserPostNotFoundException;
import com.yello.server.domain.vote.exception.VoteForbiddenException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.infrastructure.slack.factory.SlackWebhookMessageFactory;
import jakarta.servlet.http.HttpServletRequest;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    private final SlackApi slackErrorApi;
    private final SlackWebhookMessageFactory slackWebhookMessageFactory;
    private final TaskExecutor taskExecutor;

    public ControllerExceptionAdvice(
        @Qualifier("slackErrorApi") SlackApi slackErrorApi,
        SlackWebhookMessageFactory slackWebhookMessageFactory,
        @Qualifier("threadPoolTaskExecutor") TaskExecutor taskExecutor
    ) {
        this.slackWebhookMessageFactory = slackWebhookMessageFactory;
        this.taskExecutor = taskExecutor;
        this.slackErrorApi = slackErrorApi;
    }

    @ExceptionHandler(Exception.class)
    void handleException(HttpServletRequest request, Exception exception) throws Exception {
        SlackMessage slackMessage = slackWebhookMessageFactory.generateSlackErrorMessage(
            request,
            exception
        );
        Runnable runnable = () -> slackErrorApi.call(slackMessage);
        taskExecutor.execute(runnable);
        throw exception;
    }

    @ExceptionHandler({
        // @Valid 오류 Catch
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(BindException exception) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(BaseResponse.error(FIELD_REQUIRED_EXCEPTION,
                FIELD_REQUIRED_EXCEPTION.getMessage()));
    }

    @ExceptionHandler({
        // Post인데 @RequestBody 없을 때
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(
        HttpMessageConversionException exception) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(BaseResponse.error(FIELD_REQUIRED_EXCEPTION,
                FIELD_REQUIRED_EXCEPTION.getMessage()));
    }

    @ExceptionHandler({
        // @RequestParam 이 없을 때
        MissingServletRequestParameterException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(
        MissingServletRequestParameterException exception) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(BaseResponse.error(QUERY_STRING_REQUIRED_EXCEPTION,
                QUERY_STRING_REQUIRED_EXCEPTION.getMessage()));
    }

    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(
        MethodArgumentTypeMismatchException exception
    ) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(BaseResponse.error(METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION,
                METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION.getMessage()));
    }

    /**
     * 400 BAD REQUEST
     */
    @ExceptionHandler({
        FriendException.class,
        UserException.class,
        AuthBadRequestException.class,
        UserBadRequestException.class,
        QuestionException.class,
        PurchaseException.class,
        GoogleBadRequestException.class,
        AppleBadRequestException.class,
        UserAdminBadRequestException.class,
        EnumIllegalArgumentException.class,
        IllegalArgumentException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(CustomException exception) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }


    /**
     * 401 UNAUTHORIZED
     */
    @ExceptionHandler({
        CustomAuthenticationException.class,
        ExpiredTokenException.class,
        InvalidTokenException.class,
        OAuthException.class
    })
    public ResponseEntity<BaseResponse> UnauthorizedException(CustomException exception) {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    /**
     * 403 FORBIDDEN
     */
    @ExceptionHandler({
        VoteForbiddenException.class,
        NotSignedInException.class,
        NotExpiredTokenForbiddenException.class,
        NotValidTokenForbiddenException.class,
        EventForbiddenException.class
    })
    public ResponseEntity<BaseResponse> ForbiddenException(CustomException exception) {
        return ResponseEntity.status(FORBIDDEN)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    /**
     * 404 NOT FOUND
     */
    @ExceptionHandler({
        UserNotFoundException.class,
        VoteNotFoundException.class,
        GroupNotFoundException.class,
        FriendNotFoundException.class,
        QuestionNotFoundException.class,
        PurchaseNotFoundException.class,
        GoogleTokenNotFoundException.class,
        UserAdminNotFoundException.class,
        NoticeNotFoundException.class,
        AdminConfigurationNotFoundException.class,
        EventNotFoundException.class,
        EventBadRequestException.class,
        StatisticsNotFoundException.class,
        UserPostNotFoundException.class
    })
    public ResponseEntity<BaseResponse> NotFoundException(CustomException exception) {
        return ResponseEntity.status(NOT_FOUND)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    /**
     * 409 CONFLICT
     */
    @ExceptionHandler({
        UserConflictException.class,
        SubscriptionConflictException.class,
        PurchaseConflictException.class
    })
    public ResponseEntity<BaseResponse> ConflictException(CustomException exception) {
        return ResponseEntity.status(CONFLICT)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }
}
