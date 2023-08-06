package com.recipe.app.src.ingredient.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IngredientCategory {

    private final Long ingredientCategoryId;
    private final String ingredientCategoryName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public IngredientCategory(Long ingredientCategoryId, String ingredientCategoryName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.ingredientCategoryId = ingredientCategoryId;
        this.ingredientCategoryName = ingredientCategoryName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
