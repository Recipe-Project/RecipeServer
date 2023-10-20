package com.recipe.app.src.fridge.application.dto;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FridgeDto {

    @Schema(description = "냉장고 수정 요청 DTO")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeRequest {
        @Schema(description = "유통기한")
        private LocalDate expiredAt;
        @Schema(description = "수량")
        private float quantity;
        @Schema(description = "단위")
        private String unit;
    }

    @Schema(description = "냉장고 목록 응답 DTO")
    @Getter
    @Builder
    public static class FridgesResponse {
        @Schema(description = "카테고리별 냉장고 목록")
        private List<FridgeIngredientCategoryResponse> fridgeIngredientCategories;
        @Schema(description = "냉장고 바구니 갯수")
        private long fridgeBasketCount;

        public static FridgesResponse from(long fridgeBasketCount, List<Fridge> fridges) {
            Map<IngredientCategory, List<Fridge>> fridgesGroupByIngredientCategory = fridges.stream()
                    .collect(Collectors.groupingBy(f -> f.getIngredient().getIngredientCategory()));
            return FridgesResponse.builder()
                    .fridgeIngredientCategories(fridgesGroupByIngredientCategory.keySet().stream()
                            .map(category -> FridgeIngredientCategoryResponse.from(category, fridgesGroupByIngredientCategory.get(category)))
                            .collect(Collectors.toList()))
                    .fridgeBasketCount(fridgeBasketCount)
                    .build();
        }
    }

    @Schema(description = "카테고리별 냉장고 응답 DTO")
    @Getter
    @Builder
    public static class FridgeIngredientCategoryResponse {
        @Schema(description = "재료 카테고리 고유 번호")
        private Long ingredientCategoryId;
        @Schema(description = "재료 카테고리명")
        private String ingredientCategoryName;
        @Schema(description = "냉장고 목록")
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

    @Schema(description = "냉장고 응답 DTO")
    @Getter
    @Builder
    public static class FridgeResponse {
        @Schema(description = "냉장고 고유 번호")
        private Long fridgeId;
        @Schema(description = "재료명")
        private String ingredientName;
        @Schema(description = "재료 아이콘 url")
        private String ingredientIconUrl;
        @Schema(description = "유통기한")
        private String expiredAt;
        @Schema(description = "수량")
        private float quantity;
        @Schema(description = "단위")
        private String unit;
        @Schema(description = "신선도")
        private String freshness;

        public static FridgeResponse from(Fridge fridge) {
            return FridgeResponse.builder()
                    .fridgeId(fridge.getFridgeId())
                    .ingredientName(fridge.getIngredient().getIngredientName())
                    .ingredientIconUrl(fridge.getIngredient().getIngredientIconUrl())
                    .expiredAt(fridge.getExpiredAt() != null ? fridge.getExpiredAt().format(DateTimeFormatter.ofPattern("yy.MM.dd 까지")) : null)
                    .quantity(fridge.getQuantity())
                    .unit(fridge.getUnit())
                    .freshness(fridge.getFreshness().getName())
                    .build();
        }
    }

}
