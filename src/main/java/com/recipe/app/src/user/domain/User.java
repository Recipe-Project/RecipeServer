package com.recipe.app.src.user.domain;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.Ingredient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
    private final List<Fridge> fridges;
    private final List<FridgeBasket> fridgeBaskets;

    @Builder
    public User(Long userId, String socialId, String profileImgUrl, String nickname, String email, String phoneNumber, String deviceToken,
                LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime recentLoginAt, List<Fridge> fridges, List<FridgeBasket> fridgeBaskets) {
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
        this.fridges = fridges;
        this.fridgeBaskets = fridgeBaskets;
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
                .fridges(fridges)
                .fridgeBaskets(fridgeBaskets)
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
                .fridges(fridges)
                .fridgeBaskets(fridgeBaskets)
                .build();
    }

    public int getFridgeBasketCount() {
        return fridgeBaskets.size();
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
                .fridges(fridges)
                .fridgeBaskets(fridgeBaskets)
                .build();
    }

    public boolean hasIngredientInFridges(Ingredient ingredient) {
        return fridges.stream()
                .map(Fridge::getIngredient)
                .anyMatch(i -> i.equals(ingredient));
    }
}
