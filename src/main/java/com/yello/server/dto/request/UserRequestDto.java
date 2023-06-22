package com.yello.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "유저 생성 DTO")
public class UserRequestDto {
    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "닉네임 형식에 맞지 않습니다")
    @Schema(description = "유저 닉네임")
    private String nickname;
}
