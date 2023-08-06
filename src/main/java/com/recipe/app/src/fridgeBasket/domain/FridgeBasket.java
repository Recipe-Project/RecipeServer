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
}
