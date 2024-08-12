package com.recipe.app.src.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 토큰 정보 요청 DTO")
@Getter
@NoArgsConstructor
public class UserLoginRequest {

    @Schema(description = "로그인 액세스 토큰")
    private String accessToken;
    @Schema(description = "FCM 토큰")
    private String fcmToken;

    @Builder
    public UserLoginRequest(String accessToken, String fcmToken) {

        this.accessToken = accessToken;
        this.fcmToken = fcmToken;
    }
}
