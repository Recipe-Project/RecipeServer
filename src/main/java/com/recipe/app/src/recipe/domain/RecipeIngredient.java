package com.recipe.app.src.recipe.domain;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.ingredient.domain.Ingredient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RecipeIngredient {

    private final Long recipeIngredientId;
    private final Recipe recipe;
    private final Ingredient ingredient;
    private final String capacity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public RecipeIngredient(Long recipeIngredientId, Recipe recipe, Ingredient ingredient, String capacity,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.recipeIngredientId = recipeIngredientId;
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.capacity = capacity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RecipeIngredient from(Recipe recipe, Ingredient ingredient, String capacity) {
        LocalDateTime now = LocalDateTime.now();
        return RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .capacity(capacity)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public boolean isInFridges(List<Fridge> fridges) {
        return fridges.stream()
                .anyMatch(f -> f.getIngredient().equals(ingredient));
    }
}
