package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "레시피 재료 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeIngredientRequest {

    @Schema(description = "레시피 재료명")
    private String ingredientName;
    @Schema(description = "레시피 재료 아이콘 고유 번호")
    private Long ingredientIconId;
    @Schema(description = "레시피 재료 용량", nullable = true)
    private String quantity;
    @Schema(description = "레시피 재료 단위", nullable = true)
    private String unit;

    public RecipeIngredient toEntity(Recipe recipe) {

        return RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName(ingredientName)
                .ingredientIconId(ingredientIconId)
                .quantity(quantity)
                .unit(unit)
                .build();
    }
}