package com.recipe.app.src.fridgeBasket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "재료 선택 목록 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FridgeBasketIngredientIdsRequest {

    @Schema(description = "재료 고유 번호 목록")
    private List<Long> ingredientIds;
}
