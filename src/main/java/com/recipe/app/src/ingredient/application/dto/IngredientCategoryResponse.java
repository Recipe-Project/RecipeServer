package com.recipe.app.src.ingredient.application.dto;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Schema(description = "재료 카테고리 응답 DTO")
public class IngredientCategoryResponse {
    @Schema(description = "재료 카테고리 고유 번호")
    private final Long ingredientCategoryId;
    @Schema(description = "재료 카테고리명")
    private final String ingredientCategoryName;
    @Schema(description = "재료 목록")
    private final List<IngredientResponse> ingredients;

    @Builder
    public IngredientCategoryResponse(Long ingredientCategoryId, String ingredientCategoryName, List<IngredientResponse> ingredients) {

        this.ingredientCategoryId = ingredientCategoryId;
        this.ingredientCategoryName = ingredientCategoryName;
        this.ingredients = ingredients;
    }

    public static IngredientCategoryResponse from(IngredientCategory category, List<Ingredient> ingredients) {

        return IngredientCategoryResponse.builder()
                .ingredientCategoryId(category.getIngredientCategoryId())
                .ingredientCategoryName(category.getIngredientCategoryName())
                .ingredients(ingredients.stream()
                        .map(IngredientResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}