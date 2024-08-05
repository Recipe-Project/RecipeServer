package com.recipe.app.src.common.client.google.dto;

import com.recipe.app.src.user.application.dto.UserLoginRequest;
import lombok.Getter;

@Getter
public class GoogleAccessTokenResponse {

    private String id_token;

    public UserLoginRequest toLoginRequest() {

        return UserLoginRequest.builder()
                .accessToken(id_token)
                .build();
    }
}
