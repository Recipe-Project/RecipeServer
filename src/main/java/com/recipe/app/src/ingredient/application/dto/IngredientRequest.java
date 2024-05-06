package com.recipe.app.src.ingredient.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "재료 추가 요청 DTO")
@Getter
@NoArgsConstructor
public class IngredientRequest {

    @Schema(description = "재료명")
    private String ingredientName;
    @Schema(description = "재료 아이콘 고유 번호")
    private Long ingredientIconId;
    @Schema(description = "재료 카테고리 고유 번호")
    private Long ingredientCategoryId;
}
