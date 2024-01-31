package com.yello.server.domain.user.dto.response;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserDataType;
import com.yello.server.global.common.util.ConstantUtil;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import javax.annotation.Nullable;
import lombok.Builder;

@Builder
public record UserDataResponse(
    String tag,
    String value
) {

    public static UserDataResponse of(User user, UserDataType tag, @Nullable String value)
        throws DateTimeParseException {
        String resultValue = "";

        switch (tag) {
            case ACCOUNT_UPDATED_AT -> {
                if (value == null) {
                    resultValue = String.format(
                        "%s|%s|%s",
                        true,
                        null,
                        user.getCreatedAt().format(ISO_LOCAL_DATE)
                    );
                } else {
                    final ZonedDateTime current = ZonedDateTime.now(ConstantUtil.GlobalZoneId);
                    final ZonedDateTime updatedAt = ZonedDateTime.parse(value, ISO_OFFSET_DATE_TIME);
                    boolean isUpdatable = current.getYear() - updatedAt.getYear() >= 1;

                    resultValue = String.format(
                        "%s|%s|%s",
                        isUpdatable,
                        updatedAt.format(ISO_LOCAL_DATE),
                        user.getCreatedAt().format(ISO_LOCAL_DATE)
                    );
                }
            }
            case RECOMMENDED, WITHDRAW_REASON -> {
                resultValue = value;
            }
        }

        return UserDataResponse.builder()
            .tag(tag.name())
            .value(resultValue)
            .build();
    }
}
