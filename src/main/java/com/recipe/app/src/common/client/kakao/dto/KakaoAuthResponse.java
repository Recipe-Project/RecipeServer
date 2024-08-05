package com.recipe.app.src.common.client.kakao.dto;

import com.recipe.app.src.user.domain.User;
import lombok.Getter;

@Getter
public class KakaoAuthResponse {

    private Long id;
    private KakaoAuthInfoResponse kakao_account;

    public User toEntity(String fcmToken) {

        return User.builder()
                .socialId("kakao_" + id)
                .nickname(kakao_account.getNickname())
                .email(kakao_account.getEmail())
                .deviceToken(fcmToken)
                .build();
    }
}
