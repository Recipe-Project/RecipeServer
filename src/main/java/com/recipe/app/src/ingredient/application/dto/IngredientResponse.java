package com.recipe.app.src.ingredient.application.dto;

import com.recipe.app.src.ingredient.domain.Ingredient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "재료 응답 DTO")
@Getter
public class IngredientResponse {

    @Schema(description = "재료 고유 번호")
    private final Long ingredientId;
    @Schema(description = "재료명")
    private final String ingredientName;
    @Schema(description = "재료 아이콘 url")
    private final String ingredientIconUrl;

    @Builder
    public IngredientResponse(Long ingredientId, String ingredientName, String ingredientIconUrl) {

        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.ingredientIconUrl = ingredientIconUrl;
    }

    public static IngredientResponse from(Ingredient ingredient) {

        return IngredientResponse.builder()
                .ingredientId(ingredient.getIngredientId())
                .ingredientName(ingredient.getIngredientName())
                .ingredientIconUrl(ingredient.getIngredientIconUrl())
                .build();
    }
}