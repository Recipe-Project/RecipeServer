package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.ingredient.domain.Ingredient;
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
    private final String recipeIngredientName;
    @Schema(description = "레시피 재료 아이콘 url")
    private final String recipeIngredientIconUrl;
    @Schema(description = "레시피 재료 용량")
    private final String recipeIngredientCapacity;
    @Schema(description = "레시피 재료 냉장고 존재 여부")
    private final Boolean isInUserFridge;

    @Builder
    public RecipeIngredientResponse(Long recipeIngredientId, String recipeIngredientName, String recipeIngredientIconUrl,
                                    String recipeIngredientCapacity, Boolean isInUserFridge) {

        this.recipeIngredientId = recipeIngredientId;
        this.recipeIngredientName = recipeIngredientName;
        this.recipeIngredientIconUrl = recipeIngredientIconUrl;
        this.recipeIngredientCapacity = recipeIngredientCapacity;
        this.isInUserFridge = isInUserFridge;
    }

    public static RecipeIngredientResponse from(RecipeIngredient recipeIngredient, Ingredient ingredient, boolean isInUserFridge) {

        return RecipeIngredientResponse.builder()
                .recipeIngredientId(recipeIngredient.getRecipeIngredientId())
                .recipeIngredientName(ingredient.getIngredientName())
                .recipeIngredientIconUrl(ingredient.getIngredientIconUrl())
                .recipeIngredientCapacity(recipeIngredient.getCapacity())
                .isInUserFridge(isInUserFridge)
                .build();
    }
}
