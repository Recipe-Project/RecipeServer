package com.recipe.app.src.common.client.dto;

import com.recipe.app.src.user.domain.User;
import lombok.Getter;

@Getter
public class NaverAuthResponse {

    private String resultcode;
    private NaverAuthInfoResponse response;

    public User toEntity(String fcmToken) {
        return response.toEntity(fcmToken);
    }
}
