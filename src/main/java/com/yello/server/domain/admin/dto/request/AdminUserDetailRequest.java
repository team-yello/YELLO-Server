package com.yello.server.domain.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
import javax.validation.constraints.NotNull;

public record AdminUserDetailRequest(
    @JsonIgnore Long id,
    @NotNull Long recommendCount,
    @NotNull String name,
    @NotNull String yelloId,
    @NotNull Gender gender,
    @NotNull Integer point,
    @NotNull Social social,
    @NotNull String profileImage,
    @NotNull String uuid,
    @JsonIgnore @NotNull String deletedAt,
    @JsonIgnore @NotNull Long groupId,
    @NotNull int groupAdmissionYear,
    @NotNull String email,
    @NotNull Integer ticketCount,
    @NotNull String deviceToken,
    @NotNull Subscribe subscribe
) {

}
