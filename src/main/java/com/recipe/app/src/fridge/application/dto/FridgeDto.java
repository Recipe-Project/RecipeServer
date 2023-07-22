package com.recipe.app.src.fridge.application.dto;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.recipe.domain.RecipeInfo;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FridgeDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgesRequest {
        private List<FridgeRequest> fridgeBasketList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeRequest {
        private String ingredientName;
        private String ingredientIcon;
        private Integer ingredientCategoryIdx;
        private String expiredAt;
        private String storageMethod;
        private int count;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PatchFridgesRequest {
        private List<PatchFridgeRequest> patchFridgeList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PatchFridgeRequest {
        private String ingredientName;
        private String expiredAt;
        private String storageMethod;
        private Integer count;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeIngredientsRequest {
        private List<String> ingredientName;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgesResponse {
        private final List<FridgeIngredientCategoryResponse> fridges = new ArrayList<>();
        private long fridgeBasketCount;

        public FridgesResponse(long fridgeBasketCount, List<Fridge> fridges) {
            this.fridgeBasketCount = fridgeBasketCount;
            Map<IngredientCategory, List<Fridge>> fridgesMappedByCategory = fridges.stream()
                    .collect(Collectors.groupingBy(Fridge::getIngredientCategory));
            for (IngredientCategory ingredientCategory : fridgesMappedByCategory.keySet()) {
                this.fridges.add(new FridgeIngredientCategoryResponse(ingredientCategory, fridgesMappedByCategory.get(ingredientCategory)));
            }
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeIngredientCategoryResponse {
        private Integer ingredientCategoryIdx;
        private String ingredientCategoryName;
        private List<FridgeIngredientResponse> ingredientList;

        public FridgeIngredientCategoryResponse(IngredientCategory ingredientCategory, List<Fridge> fridges) {
            this.ingredientCategoryIdx = ingredientCategory.getIngredientCategoryIdx();
            this.ingredientCategoryName = ingredientCategory.getName();
            this.ingredientList = fridges.stream()
                    .map(FridgeIngredientResponse::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeIngredientResponse {
        private String ingredientName;
        private String ingredientIcon;
        private String expiredAt;
        private String storageMethod;
        private Integer count;
        private Integer freshness;

        public FridgeIngredientResponse(Fridge fridge) {
            this.ingredientName = fridge.getIngredientName();
            this.ingredientIcon = fridge.getIngredientIcon();
            this.expiredAt = fridge.getExpiredAt() != null ? fridge.getExpiredAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "까지" : null;
            this.storageMethod = fridge.getStorageMethod();
            this.count = fridge.getCount();
            this.freshness = fridge.getFreshness();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeResponse {
        private String ingredientName;
        private String ingredientIcon;
        private Integer ingredientCategoryIdx;
        private String expiredAt;
        private String storageMethod;
        private int count;

        public FridgeResponse(Fridge fridge) {
            this.ingredientName = fridge.getIngredientName();
            this.ingredientIcon = fridge.getIngredientIcon();
            this.ingredientCategoryIdx = fridge.getIngredientCategory().getIngredientCategoryIdx();
            this.expiredAt = fridge.getExpiredAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            this.storageMethod = fridge.getStorageMethod();
            this.count = fridge.getCount();
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
