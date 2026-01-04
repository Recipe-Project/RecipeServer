package com.recipe.app.src.common.client.apple.dto;

import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppleAuthResponse {

    private String sub;
    private String email;
    private String name;

    public User toEntity(String fcmToken) {

        return User.builder()
                .socialId("apple_" + sub)
                .nickname(name != null ? name : "")
                .email(email)
                .deviceToken(fcmToken)
                .build();
    }
}
