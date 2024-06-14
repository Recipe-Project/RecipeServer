package com.recipe.app.src.ingredient.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "재료 생성 응답 DTO")
@Getter
public class IngredientCreateResponse {

    @Schema(description = "재료 고유 번호")
    private final Long ingredientId;

    public IngredientCreateResponse(Long ingredientId) {

        this.ingredientId = ingredientId;
    }
}
