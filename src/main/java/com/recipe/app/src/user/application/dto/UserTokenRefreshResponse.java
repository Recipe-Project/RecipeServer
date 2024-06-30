package com.recipe.app.src.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "토큰 재발급 응답 DTO")
@Getter
@Builder
public class UserTokenRefreshResponse {

    @Schema(description = "회원 고유번호")
    private Long userId;
    @Schema(description = "액세스 토큰")
    private String accessToken;
}
