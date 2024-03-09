package com.yello.server.infrastructure.firebase.dto.request;

import com.google.firebase.messaging.Notification;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.infrastructure.firebase.dto.NotificationType;
import java.text.MessageFormat;
import lombok.Builder;

@Builder
public record NotificationMessage(
    String title,
    String message,
    NotificationType type
) {

    public static NotificationMessage toRecommendNotificationContent(User user) {
        return NotificationMessage.builder()
            .title("40분 대기 초기화 + 100포인트 적립")
            .message(MessageFormat.format("{0}님이 나를 추천인으로 가입해 옐로하기 대기 초기화 + 100포인트가 적립되었어요!",
                user.getName()))
            .type(NotificationType.RECOMMEND)
            .build();
    }

    public static NotificationMessage toVoteAvailableNotificationContent() {
        return NotificationMessage.builder()
            .title("친구에게 쪽지 보내고 포인트 받기")
            .message("대기시간이 다 지났어요. 친구들에게 투표해봐요!")
            .type(NotificationType.VOTE_AVAILABLE)
            .build();
    }

    public static NotificationMessage toFriendNotificationContent(User user) {
        return NotificationMessage.builder()
            .title(MessageFormat.format("{0}님이 회원님과 친구가 되었어요", user.getName()))
            .message("친구와 쪽지를 주고받아 보세요!")
            .type(NotificationType.NEW_FRIEND)
            .build();
    }

    public static NotificationMessage toYelloNotificationContent(Vote vote) {
        final User sender = vote.getSender();

        final String target =
            Gender.MALE.getInitial().equals(sender.getGender().getInitial()) ? "남학생" : "여학생";
        return NotificationMessage.builder()
            .title(MessageFormat.format("{0}이 쪽지를 보냈어요!", target))
            .message(vote.getQuestion().toNotificationSentence())
            .type(NotificationType.NEW_VOTE)
            .build();
    }

    public static NotificationMessage toUserOpenVoteNotificationContent(User user) {
        return NotificationMessage.builder()
                .title(MessageFormat.format("{0}님이 내가 보낸 쪽지를 확인했어요!", user.getName()))
                .message("\uD83D\uDEA8\uD83D\uDC9A 그린라이트입니다.")
                .type(NotificationType.OPEN_VOTE)
                .build();
    }

    public static NotificationMessage toUserAndFriendRecommendSignupAndGetTicketNotificationContent(User user) {
        return NotificationMessage.builder()
                .title(MessageFormat.format("{0}님이 나를 추천인으로 가입해 열람권이 지급됐어요!", user.getName()))
                .message("지금이다! 날 짝사랑 하는 사람 보러가기")
                .type(NotificationType.FIRST_RECOMMEND)
                .build();
    }

    public static NotificationMessage toAllUserLunchEventNotificationContent() {
        return NotificationMessage.builder()
                .title("우리 학교 선착순 30명 열람권 뿌린다!")
                .message("지금부터 14시까지\uD83D\uDD25 사라지기 전에 바로 확인해보세요!")
                .type(NotificationType.LUNCH_EVENT)
                .build();
    }

    public static NotificationMessage toYelloNotificationCustomContent(
        NotificationCustomMessage message) {

        return NotificationMessage.builder()
            .title(message.title())
            .message(message.message())
            .type(NotificationType.NEW_VOTE)
            .build();
    }

    public Notification toNotification() {
        return Notification.builder()
            .setTitle(title)
            .setBody(message)
            .build();
    }
}
