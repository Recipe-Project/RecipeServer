package com.recipe.app.src.common.client.kakao.dto;

import com.recipe.app.src.user.application.dto.UserLoginRequest;
import lombok.Getter;

@Getter
public class KakaoAccessTokenResponse {

    private String access_token;

    public UserLoginRequest toLoginRequest() {

        return UserLoginRequest.builder()
                .accessToken(access_token)
                .build();
    }
}
