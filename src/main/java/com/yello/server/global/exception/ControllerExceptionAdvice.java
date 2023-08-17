package com.yello.server.global.exception;

import static com.yello.server.global.common.ErrorCode.FIELD_REQUIRED_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.QUERY_STRING_REQUIRED_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.authorization.exception.CustomAuthenticationException;
import com.yello.server.domain.authorization.exception.ExpiredTokenException;
import com.yello.server.domain.authorization.exception.InvalidTokenException;
import com.yello.server.domain.authorization.exception.NotExpiredTokenForbiddenException;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.authorization.exception.NotValidTokenForbiddenException;
import com.yello.server.domain.authorization.exception.OAuthException;
import com.yello.server.domain.authorization.service.TokenProvider;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.exception.FriendNotFoundException;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.purchase.exception.PurchaseException;
import com.yello.server.domain.purchase.exception.PurchaseNotFoundException;
import com.yello.server.domain.purchase.exception.SubscriptionConflictException;
import com.yello.server.domain.question.exception.QuestionException;
import com.yello.server.domain.question.exception.QuestionNotFoundException;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserBadRequestException;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.exception.VoteForbiddenException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.infrastructure.redis.exception.RedisException;
import com.yello.server.infrastructure.redis.exception.RedisNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionAdvice {

    private final TaskExecutor taskExecutor;
    private final SlackApi slackApi;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @ExceptionHandler(Exception.class)
    void handleException(HttpServletRequest request, Exception exception) throws Exception {
        SlackAttachment slackAttachment = new SlackAttachment();

        slackAttachment.setFallback("Error");
        slackAttachment.setColor("danger");
        slackAttachment.setTitle("긴급 환자가 이송되었습니다");
        slackAttachment.setTitleLink(request.getContextPath());
        slackAttachment.setText(Arrays.toString(exception.getStackTrace()));
        slackAttachment.setColor("danger");

        List<SlackField> slackFieldList = new ArrayList<>();
        slackFieldList.add(
            new SlackField().setTitle("Request URL").setValue(request.getRequestURL().toString()));
        slackFieldList.add(
            new SlackField().setTitle("Request Method").setValue(request.getMethod()));
        slackFieldList.add(new SlackField().setTitle("Request Time").setValue(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())));
        slackFieldList.add(
            new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr()));
        slackFieldList.add(
            new SlackField().setTitle("Request User-Agent")
                .setValue(request.getHeader(HttpHeaders.USER_AGENT)));
        slackFieldList.add(
            new SlackField().setTitle("인증/인가 정보 - Authorization")
                .setValue(request.getHeader(HttpHeaders.AUTHORIZATION)));

        final String token =
            request.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
        final Long userId = tokenProvider.getUserId(token);
        final Optional<User> user = userRepository.findById(userId);
        String userInfo = "";
        userInfo = user.map(value -> "userId : " + userId
            + "\nyelloId : " + value.getYelloId()
            + "\ndeviceToken : " + value.getDeviceToken()).orElseGet(() -> "userId : " + userId);
        slackFieldList.add(
            new SlackField().setTitle("인증/인가 정보 - 유저").setValue(userInfo));

        slackAttachment.setFields(slackFieldList);

        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackMessage.setText("긴급 환자가 이송되었습니다");
        slackMessage.setUsername("옐로 소방서");

        Runnable runnable = () -> slackApi.call(slackMessage);
        taskExecutor.execute(runnable);
        throw exception;
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
        PurchaseException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(CustomException exception) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
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
        NotValidTokenForbiddenException.class
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
        RedisNotFoundException.class,
        PurchaseNotFoundException.class
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
        SubscriptionConflictException.class
    })
    public ResponseEntity<BaseResponse> ConflictException(CustomException exception) {
        return ResponseEntity.status(CONFLICT)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    /**
     * 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler({
        RedisException.class,
    })
    public ResponseEntity<BaseResponse> InternalServerException(CustomException exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }
}
