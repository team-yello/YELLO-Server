package com.yello.server.infrastructure.firebase.dto.request;

import com.google.firebase.messaging.Notification;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.User;
import com.yello.server.infrastructure.firebase.dto.NotificationType;
import java.text.MessageFormat;
import lombok.Builder;

@Builder
public record NotificationMessage(
    String title,
    String message,
    NotificationType type
) {

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

    public static NotificationMessage toYelloNotificationContent(User user) {
        final String target = Gender.MALE.getIntial().equals(user.getGender().getIntial()) ? "남학생" : "여학생";
        return NotificationMessage.builder()
            .title(MessageFormat.format("{0}이 쪽지를 보냈어요!", target))
            .message("나는 너가 ...")
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
