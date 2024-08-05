package com.recipe.app.src.common.client.dto;

import com.recipe.app.src.user.domain.User;
import lombok.Getter;

@Getter
public class NaverAuthInfoResponse {

    private String id;
    private String name;
    private String email;
    private String mobile;

    public User toEntity(String fcmToken) {

        return User.builder()
                .socialId("naver_" + id)
                .nickname(name)
                .email(email)
                .phoneNumber(mobile)
                .deviceToken(fcmToken)
                .build();
    }
}
