package com.recipe.app.src.ingredient.application.dto;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Schema(description = "재료 목록 응답 DTO")
public class IngredientsResponse {
    @Schema(description = "냉장고 바구니 갯수")
    private final long fridgeBasketCount;
    @Schema(description = "카테고리별 재료 목록")
    private final List<IngredientCategoryResponse> ingredientCategories;

    @Builder
    public IngredientsResponse(long fridgeBasketCount, List<IngredientCategoryResponse> ingredientCategories) {

        this.fridgeBasketCount = fridgeBasketCount;
        this.ingredientCategories = ingredientCategories;
    }

    public static IngredientsResponse from(long fridgeBasketCount, List<IngredientCategory> categories, List<Ingredient> ingredients) {

        Map<Long, List<Ingredient>> ingredientsMapByIngredientCategoryId = ingredients.stream()
                .collect(Collectors.groupingBy(Ingredient::getIngredientCategoryId));

        return IngredientsResponse.builder()
                .fridgeBasketCount(fridgeBasketCount)
                .ingredientCategories(categories.stream()
                        .map(category -> IngredientCategoryResponse.from(category, ingredientsMapByIngredientCategoryId.getOrDefault(category.getIngredientCategoryId(), new ArrayList<>())))
                        .collect(Collectors.toList()))
                .build();
    }
}