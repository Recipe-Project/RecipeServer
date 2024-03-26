package com.recipe.app.src.user.application.dto;

import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "소셜 로그인 응답 DTO")
@Getter
public class UserSocialLoginResponse {

    @Schema(description = "jwt")
    private final String jwt;
    @Schema(description = "회원 고유번호")
    private final Long userId;

    @Builder
    public UserSocialLoginResponse(String jwt, Long userId) {

        this.jwt = jwt;
        this.userId = userId;
    }

    public static UserSocialLoginResponse from(User user, String jwt) {
        return UserSocialLoginResponse.builder()
                .jwt(jwt)
                .userId(user.getUserId())
                .build();
    }
}
