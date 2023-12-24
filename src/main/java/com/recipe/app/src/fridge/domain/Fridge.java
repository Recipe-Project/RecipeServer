package com.recipe.app.src.fridge.domain;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class Fridge {

    private final Long fridgeId;
    private final User user;
    private final Ingredient ingredient;
    private final LocalDateTime expiredAt;
    private final float quantity;
    private final String unit;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public Fridge(Long fridgeId, User user, Ingredient ingredient, LocalDateTime expiredAt, float quantity, String unit, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.fridgeId = fridgeId;
        this.user = user;
        this.ingredient = ingredient;
        this.expiredAt = expiredAt;
        this.quantity = quantity;
        this.unit = unit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Fridge from(FridgeBasket fridgeBasket) {
        LocalDateTime now = LocalDateTime.now();
        return Fridge.builder()
                .user(fridgeBasket.getUser())
                .ingredient(fridgeBasket.getIngredient())
                .expiredAt(fridgeBasket.getExpiredAt())
                .quantity(fridgeBasket.getQuantity())
                .unit(fridgeBasket.getUnit())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Freshness getFreshness() {
        if (this.expiredAt == null) {
            return Freshness.FRESH;
        }
        long diffDay = ChronoUnit.DAYS.between(LocalDate.now(), expiredAt);
        if (diffDay <= 0) {
            return Freshness.DISPOSAL;
        }
        if (diffDay < 7) {
            return Freshness.DANGER;
        }
        return Freshness.FRESH;
    }

    public Fridge changeExpiredAtAndQuantityAndUnit(LocalDateTime expiredAt, float quantity, String unit) {
        LocalDateTime now = LocalDateTime.now();
        return Fridge.builder()
                .fridgeId(fridgeId)
                .user(user)
                .ingredient(ingredient)
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .createdAt(getCreatedAt())
                .updatedAt(now)
                .build();
    }

    public Fridge plusQuantity(float quantity) {
        LocalDateTime now = LocalDateTime.now();
        return Fridge.builder()
                .fridgeId(fridgeId)
                .user(user)
                .ingredient(ingredient)
                .expiredAt(expiredAt)
                .quantity(this.quantity + quantity)
                .unit(unit)
                .createdAt(getCreatedAt())
                .updatedAt(now)
                .build();
    }
}
