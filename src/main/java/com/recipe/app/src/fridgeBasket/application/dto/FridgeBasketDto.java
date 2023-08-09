package com.recipe.app.src.fridgeBasket.application.dto;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FridgeBasketDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketIngredientIdsRequest {
        private List<Long> ingredientIds;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketIngredientRequest {
        private String ingredientName;
        private String ingredientIconUrl;
        private Long ingredientCategoryId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketRequest {
        private LocalDate expiredAt;
        private float quantity;
        private String unit;
    }

    @Getter
    @Builder
    public static class FridgeBasketsResponse {
        private int fridgeBasketCount;
        private List<FridgeBasketIngredientCategoryResponse> ingredientCategories;

        public static FridgeBasketsResponse from(List<FridgeBasket> fridgeBaskets) {
            Map<IngredientCategory, List<FridgeBasket>> fridgeBasketsGroupByIngredientCategory = fridgeBaskets.stream()
                    .collect(Collectors.groupingBy(f -> f.getIngredient().getIngredientCategory()));
            return FridgeBasketDto.FridgeBasketsResponse.builder()
                    .fridgeBasketCount(fridgeBaskets.size())
                    .ingredientCategories(fridgeBasketsGroupByIngredientCategory.keySet().stream()
                            .map(category -> FridgeBasketIngredientCategoryResponse.from(category, fridgeBasketsGroupByIngredientCategory.get(category)))
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FridgeBasketIngredientCategoryResponse {
        private Long ingredientCategoryId;
        private String ingredientCategoryName;
        private List<FridgeBasketResponse> fridgeBaskets;

        public static FridgeBasketIngredientCategoryResponse from(IngredientCategory category, List<FridgeBasket> fridgeBaskets) {
            return FridgeBasketIngredientCategoryResponse.builder()
                    .ingredientCategoryId(category.getIngredientCategoryId())
                    .ingredientCategoryName(category.getIngredientCategoryName())
                    .fridgeBaskets(fridgeBaskets.stream()
                            .map(FridgeBasketResponse::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FridgeBasketResponse {
        private Long fridgeBasketId;
        private String ingredientName;
        private String ingredientIconUrl;
        private String expiredAt;
        private float quantity;
        private String unit;

        public static FridgeBasketResponse from(FridgeBasket fridgeBasket) {
            return FridgeBasketResponse.builder()
                    .fridgeBasketId(fridgeBasket.getFridgeBasketId())
                    .ingredientName(fridgeBasket.getIngredient().getIngredientName())
                    .ingredientIconUrl(fridgeBasket.getIngredient().getIngredientIconUrl())
                    .expiredAt(fridgeBasket.getExpiredAt().format(DateTimeFormatter.ofPattern("yy.MM.dd")))
                    .quantity(fridgeBasket.getQuantity())
                    .unit(fridgeBasket.getUnit())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketCountResponse {
        private long fridgesBasketCount;
    }
}
