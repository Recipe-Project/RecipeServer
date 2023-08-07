package com.recipe.app.src.ingredient.domain;

import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public static Ingredient from(IngredientCategory ingredientCategory, String ingredientName, String ingredientIconUrl, User user) {
        LocalDateTime now = LocalDateTime.now();
        return Ingredient.builder()
                .ingredientCategory(ingredientCategory)
                .ingredientName(ingredientName)
                .ingredientIconUrl(ingredientIconUrl)
                .createdAt(now)
                .updatedAt(now)
                .user(user)
                .isDefault(false)
                .isHidden(true)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ingredient))
            return false;
        Ingredient category = (Ingredient) o;
        return ingredientId.equals(category.getIngredientId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientId);
    }
}
