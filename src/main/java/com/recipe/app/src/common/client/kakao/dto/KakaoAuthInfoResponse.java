package com.recipe.app.src.common.client.kakao.dto;

import lombok.Getter;

@Getter
public class KakaoAuthInfoResponse {

    private String name;
    private String email;
    private KakaoProfileResponse profile;

    public String getNickname() {
        return profile.getNickname();
    }
}
