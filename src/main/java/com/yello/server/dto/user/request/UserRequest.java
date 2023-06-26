package com.yello.server.dto.user.request;

import com.yello.server.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record UserRequest(
        @NotBlank
        @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "닉네임 형식에 맞지 않습니다")
        @Schema(description = "유저 닉네임")
        String nickname
) {
    public User toEntity(User user) {
        return User.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}