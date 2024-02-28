package com.yello.server.domain.authorization.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record OnBoardingFriendRequest(
    @NotNull List<String> friendKakaoId
) {

}
