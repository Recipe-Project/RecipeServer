package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.RecipeIngredient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "레시피 재료 응답 DTO")
@Getter
public class RecipeIngredientResponse {

    @Schema(description = "레시피 재료 고유 번호")
    private final Long recipeIngredientId;
    @Schema(description = "레시피 재료명")
    private final String ingredientName;
    @Schema(description = "레시피 재료 아이콘 고유 번호")
    private final Long ingredientIconId;
    @Schema(description = "레시피 재료 용량")
    private final String quantity;
    @Schema(description = "레시피 재료 단위")
    private final String unit;
    @Schema(description = "레시피 재료 냉장고 존재 여부")
    private final Boolean isInUserFridge;

    @Builder
    public RecipeIngredientResponse(Long recipeIngredientId, String ingredientName, Long ingredientIconId, String quantity, String unit, Boolean isInUserFridge) {

        this.recipeIngredientId = recipeIngredientId;
        this.ingredientName = ingredientName;
        this.ingredientIconId = ingredientIconId;
        this.quantity = quantity;
        this.unit = unit;
        this.isInUserFridge = isInUserFridge;
    }

    public static RecipeIngredientResponse from(RecipeIngredient recipeIngredient, boolean isInUserFridge) {

        return RecipeIngredientResponse.builder()
                .recipeIngredientId(recipeIngredient.getRecipeIngredientId())
                .ingredientName(recipeIngredient.getIngredientName())
                .ingredientIconId(recipeIngredient.getIngredientIconId())
                .quantity(recipeIngredient.getQuantity())
                .unit(recipeIngredient.getUnit())
                .isInUserFridge(isInUserFridge)
                .build();
    }
}
