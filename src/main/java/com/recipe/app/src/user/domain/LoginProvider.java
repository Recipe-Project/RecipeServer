package com.recipe.app.src.user.domain;

import java.util.Arrays;

public enum LoginProvider {
    KAKAO, NAVER, GOOGLE;

    public static LoginProvider findLoginProvider(String socialId) {
        return Arrays.stream(values()).filter(provider -> socialId.toUpperCase().contains(provider.name()))
                .findAny()
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("로그인 정보를 찾을 수 없습니다.");
                });
    }
}
