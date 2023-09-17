package com.recipe.app.src.fridgeBasket.domain;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class FridgeBasket {

    private final Long fridgeBasketId;
    private final User user;
    private final Ingredient ingredient;
    private final LocalDate expiredAt;
    private final float quantity;
    private final String unit;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public FridgeBasket(Long fridgeBasketId, User user, Ingredient ingredient, LocalDate expiredAt, float quantity, String unit, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.fridgeBasketId = fridgeBasketId;
        this.user = user;
        this.ingredient = ingredient;
        this.expiredAt = expiredAt;
        this.quantity = quantity;
        this.unit = unit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static FridgeBasket from(User user, Ingredient ingredient) {
        LocalDateTime now = LocalDateTime.now();
        return FridgeBasket.builder()
                .user(user)
                .ingredient(ingredient)
                .quantity(1)
                .unit("00")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public FridgeBasket plusQuantity(float quantity) {
        LocalDateTime now = LocalDateTime.now();
        return FridgeBasket.builder()
                .fridgeBasketId(fridgeBasketId)
                .user(user)
                .ingredient(ingredient)
                .expiredAt(expiredAt)
                .quantity(this.quantity + quantity)
                .unit(unit)
                .createdAt(getCreatedAt())
                .updatedAt(now)
                .build();
    }

    public FridgeBasket changeExpiredAtAndQuantityAndUnit(LocalDate expiredAt, float quantity, String unit) {
        LocalDateTime now = LocalDateTime.now();
        return FridgeBasket.builder()
                .fridgeBasketId(fridgeBasketId)
                .user(user)
                .ingredient(ingredient)
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .createdAt(getCreatedAt())
                .updatedAt(now)
                .build();
    }
}
