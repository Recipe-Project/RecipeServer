package com.recipe.app.src.fridgeBasket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "재료 직접 입력 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FridgeBasketIngredientRequest {

    @Schema(description = "재료명")
    private String ingredientName;
    @Schema(description = "재료 아이콘 url")
    private String ingredientIconUrl;
    @Schema(description = "재료 카테고리 고유 번호")
    private Long ingredientCategoryId;
}