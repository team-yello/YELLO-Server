package com.yello.server.domain.authorization.dto.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OnBoardingFriendRequest(
    @NotNull List<String> friendKakaoId
) {

}
