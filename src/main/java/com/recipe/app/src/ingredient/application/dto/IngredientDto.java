package com.recipe.app.src.ingredient.application.dto;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "재료 목록 응답 DTO")
    public static class IngredientsResponse {
        @Schema(description = "냉장고 바구니 갯수")
        private long fridgeBasketCount;
        @Schema(description = "카테고리별 재료 목록")
        private List<IngredientCategoryResponse> ingredientCategories;

        public IngredientsResponse(long fridgeBasketCount, Map<IngredientCategory, List<Ingredient>> ingredientsGroupingByIngredientCategory) {
            this.fridgeBasketCount = fridgeBasketCount;
            this.ingredientCategories = ingredientsGroupingByIngredientCategory.keySet().stream()
                    .map((category) -> new IngredientCategoryResponse(category, ingredientsGroupingByIngredientCategory.get(category)))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class IngredientCategoryResponse {
        @Schema(description = "재료 카테고리 고유 번호")
        private Long ingredientCategoryId;
        @Schema(description = "재료 카테고리명")
        private String ingredientCategoryName;
        @Schema(description = "재료 목록")
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
        @Schema(description = "재료 고유 번호")
        private Long ingredientId;
        @Schema(description = "재료명")
        private String ingredientName;
        @Schema(description = "재료 아이콘 url")
        private String ingredientIconUrl;

        public IngredientResponse(Ingredient ingredient) {
            this(ingredient.getIngredientId(), ingredient.getIngredientName(), ingredient.getIngredientIconUrl());
        }
    }
}
