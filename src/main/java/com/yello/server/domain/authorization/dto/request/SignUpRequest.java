package com.yello.server.domain.authorization.dto.request;

import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignUpRequest(
    @NotNull Social social,
    @NotNull String uuid,
    @NotNull String deviceToken,
    @NotNull @Email String email,
    @NotNull String profileImage,
    @NotNull Long groupId,
    @NotNull int groupAdmissionYear,
    @NotNull String name,
    @NotNull String yelloId,
    @NotNull Gender gender,
    @NotNull List<Long> friends,
    String recommendId
) {

}