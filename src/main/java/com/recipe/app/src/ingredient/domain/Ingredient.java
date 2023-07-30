package com.recipe.app.src.ingredient.domain;

import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Ingredient {

    private final Long ingredientId;
    private final IngredientCategory ingredientCategory;
    private final String ingredientName;
    private final String ingredientIconUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final User user;
    private final boolean isDefault;
    private final boolean isHidden;

    @Builder
    public Ingredient(Long ingredientId, IngredientCategory ingredientCategory, String ingredientName, String ingredientIconUrl, LocalDateTime createdAt, LocalDateTime updatedAt,
                      User user, boolean isDefault, boolean isHidden) {
        this.ingredientId = ingredientId;
        this.ingredientCategory = ingredientCategory;
        this.ingredientName = ingredientName;
        this.ingredientIconUrl = ingredientIconUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
        this.isDefault = isDefault;
        this.isHidden = isHidden;
    }
}
