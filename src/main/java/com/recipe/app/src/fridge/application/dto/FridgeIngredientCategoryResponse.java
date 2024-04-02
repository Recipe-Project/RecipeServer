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

@Schema(description = "카테고리별 냉장고 응답 DTO")
@Getter
public class FridgeIngredientCategoryResponse {

    @Schema(description = "재료 카테고리 고유 번호")
    private final Long ingredientCategoryId;
    @Schema(description = "재료 카테고리명")
    private final String ingredientCategoryName;
    @Schema(description = "냉장고 목록")
    private final List<FridgeResponse> fridges;

    @Builder
    public FridgeIngredientCategoryResponse(Long ingredientCategoryId, String ingredientCategoryName, List<FridgeResponse> fridges) {

        this.ingredientCategoryId = ingredientCategoryId;
        this.ingredientCategoryName = ingredientCategoryName;
        this.fridges = fridges;
    }

    public static FridgeIngredientCategoryResponse from(IngredientCategory ingredientCategory, List<Ingredient> ingredients, List<Fridge> fridges) {

        Map<Long, Ingredient> ingredientMapById = ingredients.stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Function.identity()));

        return FridgeIngredientCategoryResponse.builder()
                .ingredientCategoryId(ingredientCategory.getIngredientCategoryId())
                .ingredientCategoryName(ingredientCategory.getIngredientCategoryName())
                .fridges(fridges.stream()
                        .map(fridge -> FridgeResponse.from(fridge, ingredientMapById.get(fridge.getIngredientId())))
                        .collect(Collectors.toList()))
                .build();
    }
}