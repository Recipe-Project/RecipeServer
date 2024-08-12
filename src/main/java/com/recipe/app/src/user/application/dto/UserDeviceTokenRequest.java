package com.recipe.app.src.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "수정할 디바이스 토큰 요청 DTO")
@Getter
@NoArgsConstructor
public class UserDeviceTokenRequest {

    @Schema(description = "FCM 디바이스 토큰")
    private String fcmToken;

    @Builder
    public UserDeviceTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
