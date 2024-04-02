package com.recipe.app.src.fridgeBasket.application.dto;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema(description = "냉장고 바구니 재료 카테고리 응답 DTO")
@Getter
public class FridgeBasketIngredientCategoryResponse {

    @Schema(description = "재료 카테고리 고유 번호")
    private final Long ingredientCategoryId;
    @Schema(description = "재료 카테고리명")
    private final String ingredientCategoryName;
    @Schema(description = "냉장고 바구니 목록")
    private final List<FridgeBasketResponse> fridgeBaskets;

    @Builder
    public FridgeBasketIngredientCategoryResponse(Long ingredientCategoryId, String ingredientCategoryName, List<FridgeBasketResponse> fridgeBaskets) {

        this.ingredientCategoryId = ingredientCategoryId;
        this.ingredientCategoryName = ingredientCategoryName;
        this.fridgeBaskets = fridgeBaskets;
    }

    public static FridgeBasketIngredientCategoryResponse from(IngredientCategory category, List<Ingredient> ingredients, List<FridgeBasket> fridgeBaskets) {

        Map<Long, Ingredient> ingredientMapById = ingredients.stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Function.identity()));

        return FridgeBasketIngredientCategoryResponse.builder()
                .ingredientCategoryId(category.getIngredientCategoryId())
                .ingredientCategoryName(category.getIngredientCategoryName())
                .fridgeBaskets(fridgeBaskets.stream()
                        .map(fridgeBasket -> FridgeBasketResponse.from(fridgeBasket, ingredientMapById.get(fridgeBasket.getIngredientId())))
                        .collect(Collectors.toList()))
                .build();
    }
}