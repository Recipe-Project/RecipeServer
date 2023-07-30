package com.recipe.app.src.ingredient.application.dto;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IngredientDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IngredientsResponse {
        private Boolean hasFridgeBaskets;
        private List<IngredientCategoryResponse> ingredients;

        public IngredientsResponse(boolean hasFridgeBaskets, Map<IngredientCategory, List<Ingredient>> ingredientsGroupingByIngredientCategory) {
            this.hasFridgeBaskets = hasFridgeBaskets;
            this.ingredients = ingredientsGroupingByIngredientCategory.keySet().stream()
                    .map((category) -> new IngredientCategoryResponse(category, ingredientsGroupingByIngredientCategory.get(category)))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class IngredientCategoryResponse {
        private Long ingredientCategoryId;
        private String ingredientCategoryName;
        private List<IngredientResponse> ingredients;

        public IngredientCategoryResponse(IngredientCategory category, List<Ingredient> ingredients) {
            this.ingredientCategoryId = category.getIngredientCategoryId();
            this.ingredientCategoryName = category.getIngredientCategoryName();
            this.ingredients = ingredients.stream()
                    .map(IngredientResponse::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IngredientResponse {
        private Long ingredientId;
        private String ingredientName;
        private String ingredientIcon;

        public IngredientResponse(Ingredient ingredient) {
            this(ingredient.getIngredientId(), ingredient.getIngredientName(), ingredient.getIngredientIconUrl());
        }
    }
}
