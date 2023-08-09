package com.recipe.app.src.fridge.application.dto;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.recipe.domain.RecipeInfo;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FridgeDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeRequest {
        private LocalDate expiredAt;
        private float quantity;
        private String unit;
    }

    @Getter
    @Builder
    public static class FridgesResponse {
        private List<FridgeIngredientCategoryResponse> fridges;
        private long fridgeBasketCount;

        public static FridgesResponse from(long fridgeBasketCount, List<Fridge> fridges) {
            Map<IngredientCategory, List<Fridge>> fridgesGroupByIngredientCategory = fridges.stream()
                    .collect(Collectors.groupingBy(f -> f.getIngredient().getIngredientCategory()));
            return FridgesResponse.builder()
                    .fridges(fridgesGroupByIngredientCategory.keySet().stream()
                            .map(category -> FridgeIngredientCategoryResponse.from(category, fridges))
                            .collect(Collectors.toList()))
                    .fridgeBasketCount(fridgeBasketCount)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FridgeIngredientCategoryResponse {
        private Long ingredientCategoryId;
        private String ingredientCategoryName;
        private List<FridgeResponse> fridges;

        public static FridgeIngredientCategoryResponse from(IngredientCategory ingredientCategory, List<Fridge> fridges) {
            return FridgeIngredientCategoryResponse.builder()
                    .ingredientCategoryId(ingredientCategory.getIngredientCategoryId())
                    .ingredientCategoryName(ingredientCategory.getIngredientCategoryName())
                    .fridges(fridges.stream()
                            .map(FridgeResponse::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FridgeResponse {
        private Long fridgeId;
        private String ingredientName;
        private String ingredientIconUrl;
        private String expiredAt;
        private float quantity;
        private String unit;
        private String freshness;

        public static FridgeResponse from(Fridge fridge) {
            return FridgeResponse.builder()
                    .fridgeId(fridge.getFridgeId())
                    .ingredientName(fridge.getIngredient().getIngredientName())
                    .ingredientIconUrl(fridge.getIngredient().getIngredientIconUrl())
                    .expiredAt(fridge.getExpiredAt().format(DateTimeFormatter.ofPattern("yy.MM.dd 까지")))
                    .quantity(fridge.getQuantity())
                    .unit(fridge.getUnit())
                    .freshness(fridge.getFreshness().getName())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeRecipesResponse {
        private int total;
        private List<FridgeRecipeResponse> recipeList;

        public FridgeRecipesResponse(int total, List<RecipeInfo> recipes) {
            this.total = total;
            this.recipeList = recipes.stream()
                    .map(FridgeRecipeResponse::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeRecipeResponse {
        private Integer recipeId;
        private String title;
        private String content;
        private String thumbnail;
        private String cookingTime;
        private long scrapCount;

        public FridgeRecipeResponse(RecipeInfo recipe) {
            this.recipeId = recipe.getRecipeId();
            this.title = recipe.getRecipeNmKo();
            this.content = recipe.getSumry();
            this.thumbnail = recipe.getImgUrl();
            this.cookingTime = recipe.getCookingTime();
            this.scrapCount = recipe.getScrapPublics().size();
        }
    }
}
