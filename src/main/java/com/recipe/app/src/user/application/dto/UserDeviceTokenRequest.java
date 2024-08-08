package com.recipe.app.src.user.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel(value = "수정할 디바이스 토큰 요청 DTO", description = "FCM 디바이스 토큰 정보")
@Getter
@NoArgsConstructor
public class UserDeviceTokenRequest {

    @ApiModelProperty(value = "FCM 디바이스 토큰")
    private String fcmToken;

    @Builder
    public UserDeviceTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
