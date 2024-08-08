package com.recipe.app.src.user.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel(description = "토큰 재발급 정보 요청 DTO")
@Getter
@NoArgsConstructor
public class UserTokenRefreshRequest {

    @ApiModelProperty(value = "회원 고유번호")
    private Long userId;
    @ApiModelProperty(value = "리프레쉬 토큰")
    private String refreshToken;

    @Builder
    public UserTokenRefreshRequest(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
