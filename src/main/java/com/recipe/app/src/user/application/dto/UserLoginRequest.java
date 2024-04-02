package com.recipe.app.src.user.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel(description = "로그인 토큰 정보 요청 DTO")
@Getter
@NoArgsConstructor
public class UserLoginRequest {

    @ApiModelProperty(value = "로그인 액세스 토큰")
    private String accessToken;
    @ApiModelProperty(value = "FCM 토큰")
    private String fcmToken;

    @Builder
    public UserLoginRequest(String accessToken, String fcmToken) {

        this.accessToken = accessToken;
        this.fcmToken = fcmToken;
    }
}
