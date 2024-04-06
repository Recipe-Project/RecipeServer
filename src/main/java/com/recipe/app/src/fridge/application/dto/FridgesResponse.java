package com.recipe.app.src.fridge.application.dto;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema(description = "냉장고 목록 응답 DTO")
@Getter
public class FridgesResponse {

    @Schema(description = "냉장고 바구니 갯수")
    private final long fridgeBasketCount;
    @Schema(description = "카테고리별 냉장고 목록")
    private final List<FridgeIngredientCategoryResponse> fridgeIngredientCategories;

    @Builder
    public FridgesResponse(long fridgeBasketCount, List<FridgeIngredientCategoryResponse> fridgeIngredientCategories) {
        this.fridgeBasketCount = fridgeBasketCount;
        this.fridgeIngredientCategories = fridgeIngredientCategories;
    }

    public static FridgesResponse from(long fridgeBasketCount, List<Fridge> fridges, List<IngredientCategory> categories, List<Ingredient> ingredients) {

        Map<Long, List<Ingredient>> ingredientsMapByCategoryId = ingredients.stream()
                .collect(Collectors.groupingBy(Ingredient::getIngredientCategoryId));

        Map<Long, Ingredient> ingredientMapById = ingredients.stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Function.identity()));
        Map<Long, List<Fridge>> fridgesMapByCategoryId = fridges.stream()
                .collect(Collectors.groupingBy(fridge -> ingredientMapById.get(fridge.getIngredientId()).getIngredientCategoryId()));

        return FridgesResponse.builder()
                .fridgeBasketCount(fridgeBasketCount)
                .fridgeIngredientCategories(categories.stream()
                        .map(category -> FridgeIngredientCategoryResponse.from(category,
                                ingredientsMapByCategoryId.getOrDefault(category.getIngredientCategoryId(), List.of()),
                                fridgesMapByCategoryId.getOrDefault(category.getIngredientCategoryId(), List.of()))
                        )
                        .collect(Collectors.toList()))
                .build();
    }
}