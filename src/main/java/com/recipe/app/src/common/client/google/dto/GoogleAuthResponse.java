package com.recipe.app.src.common.client.google.dto;

import com.recipe.app.src.user.domain.User;
import lombok.Getter;

@Getter
public class GoogleAuthResponse {

    private String sub;
    private String name;
    private String email;

    public User toEntity(String fcmToken) {

        return User.builder()
                .socialId("google_" + sub)
                .nickname(name)
                .email(email)
                .deviceToken(fcmToken)
                .build();
    }
}
