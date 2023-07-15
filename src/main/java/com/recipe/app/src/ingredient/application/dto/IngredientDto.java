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
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IngredientsResponse {
        private long fridgeBasketCount;
        private List<IngredientCategoryResponse> ingredients;

        public IngredientsResponse(long fridgeBasketCount, Map<IngredientCategory, List<Ingredient>> ingredientsGroupingByCategory) {
            this.fridgeBasketCount = fridgeBasketCount;
            this.ingredients = ingredientsGroupingByCategory.keySet().stream()
                    .map((category) -> new IngredientCategoryResponse(category, ingredientsGroupingByCategory.get(category)))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class IngredientCategoryResponse {
        private Integer ingredientCategoryIdx;
        private String ingredientCategoryName;
        private List<IngredientResponse> ingredientList;

        public IngredientCategoryResponse(IngredientCategory category, List<Ingredient> ingredients) {
            this.ingredientCategoryIdx = category.getIngredientCategoryIdx();
            this.ingredientCategoryName = category.getName();
            this.ingredientList = ingredients.stream()
                    .map(IngredientResponse::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IngredientResponse {
        private Integer ingredientIdx;
        private String ingredientName;
        private String ingredientIcon;

        public IngredientResponse(Ingredient ingredient) {
            this(ingredient.getIngredientIdx(), ingredient.getName(), ingredient.getIcon());
        }
    }
}
