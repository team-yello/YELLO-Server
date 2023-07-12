package com.yello.server.domain.authorization.dto.request;

import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Builder
public record SignInRequest(
        @NotNull Social social,
        @NotNull @Email String email,
        @NotNull String profileImage,
        @NotNull Long groupId,
        @NotNull String name,
        @NotNull String yelloId,
        @NotNull Gender gender,
        @NotNull List<Long> friends,
        String recommendId
) { }