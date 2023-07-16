package com.yello.server.domain.authorization.dto.request;

import javax.validation.constraints.NotNull;
import java.util.List;

public record OnBoardingFriendRequest(
        @NotNull List<String> friendKakaoId,
        @NotNull Long groupId
) {
}
