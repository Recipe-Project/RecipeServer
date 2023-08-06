package com.recipe.app.src.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final Long userId;
    private final String socialId;
    private final String profileImgUrl;
    private final String nickname;
    private final String email;
    private final String phoneNumber;
    private final String deviceToken;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime recentLoginAt;

    @Builder
    public User(Long userId, String socialId, String profileImgUrl, String nickname, String email, String phoneNumber, String deviceToken,
                LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime recentLoginAt) {
        this.userId = userId;
        this.socialId = socialId;
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceToken = deviceToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.recentLoginAt = recentLoginAt;
    }

    public static User from(String socialId, String profileImgUrl, String nickname, String email, String phoneNumber, String deviceToken) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .socialId(socialId)
                .profileImgUrl(profileImgUrl)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .deviceToken(deviceToken)
                .createdAt(now)
                .updatedAt(now)
                .recentLoginAt(now)
                .build();
    }

    public User changeProfile(String profileImgUrl, String nickname) {
        return User.builder()
                .userId(userId)
                .socialId(socialId)
                .profileImgUrl(profileImgUrl)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .deviceToken(deviceToken)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .recentLoginAt(recentLoginAt)
                .build();
    }

    public User changeRecentLoginAt() {
        return User.builder()
                .userId(userId)
                .socialId(socialId)
                .profileImgUrl(profileImgUrl)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .deviceToken(deviceToken)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .recentLoginAt(LocalDateTime.now())
                .build();
    }

    public int getFridgeBasketCount() {
        return 0;
    }

    public User changeDeviceToken(String fcmToken) {
        return User.builder()
                .userId(userId)
                .socialId(socialId)
                .profileImgUrl(profileImgUrl)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .deviceToken(fcmToken)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .recentLoginAt(recentLoginAt)
                .build();
    }
}
