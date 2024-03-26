package com.recipe.app.src.user.application.dto;

import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "자동 로그인 응답 DTO")
@Getter
@Builder
public class UserLoginResponse {

    @Schema(description = "회원 고유번호")
    private Long userId;

    public static UserLoginResponse from(User user) {

        return UserLoginResponse.builder()
                .userId(user.getUserId())
                .build();
    }
}