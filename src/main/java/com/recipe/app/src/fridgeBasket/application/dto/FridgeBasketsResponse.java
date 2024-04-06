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

@Schema(description = "냉장고 바구니 목록 응답 DTO")
@Getter
public class FridgeBasketsResponse {

    @Schema(description = "냉장고 바구니 목록 총 갯수")
    private final int fridgeBasketCount;
    @Schema(description = "재료 카테고리별 냉장고 바구니 목록")
    private final List<FridgeBasketIngredientCategoryResponse> ingredientCategories;

    @Builder
    public FridgeBasketsResponse(int fridgeBasketCount, List<FridgeBasketIngredientCategoryResponse> ingredientCategories) {

        this.fridgeBasketCount = fridgeBasketCount;
        this.ingredientCategories = ingredientCategories;
    }

    public static FridgeBasketsResponse from(List<FridgeBasket> fridgeBaskets, List<IngredientCategory> categories, List<Ingredient> ingredients) {

        Map<Long, List<Ingredient>> ingredientsMapByCategoryId = ingredients.stream()
                .collect(Collectors.groupingBy(Ingredient::getIngredientId));

        Map<Long, Ingredient> ingredientMapById = ingredients.stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Function.identity()));
        Map<Long, List<FridgeBasket>> fridgeBasketsMapByCategoryId = fridgeBaskets.stream()
                .collect(Collectors.groupingBy(fridgeBasket -> ingredientMapById.get(fridgeBasket.getIngredientId()).getIngredientCategoryId()));

        return FridgeBasketsResponse.builder()
                .fridgeBasketCount(fridgeBaskets.size())
                .ingredientCategories(categories.stream()
                        .map(category -> FridgeBasketIngredientCategoryResponse.from(category,
                                ingredientsMapByCategoryId.getOrDefault(category.getIngredientCategoryId(), List.of()),
                                fridgeBasketsMapByCategoryId.getOrDefault(category.getIngredientCategoryId(), List.of()))
                        )
                        .collect(Collectors.toList()))
                .build();
    }
}