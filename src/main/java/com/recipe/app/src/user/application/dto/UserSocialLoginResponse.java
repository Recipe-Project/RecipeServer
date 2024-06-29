package com.recipe.app.src.user.application.dto;

import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "소셜 로그인 응답 DTO")
@Getter
public class UserSocialLoginResponse {

    @Schema(description = "access token")
    private final String accessToken;
    @Schema(description = "refresh token")
    private final String refreshToken;
    @Schema(description = "회원 고유번호")
    private final Long userId;

    @Builder
    public UserSocialLoginResponse(String accessToken, String refreshToken, Long userId) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public static UserSocialLoginResponse from(User user, String accessToken, String refreshToken) {
        return UserSocialLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .build();
    }
}
