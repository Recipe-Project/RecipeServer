package com.recipe.app.src.fridgeBasket.application.dto;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FridgeBasketDto {

    @Schema(description = "재료 선택 목록 요청 DTO")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketIngredientIdsRequest {
        @Schema(description = "재료 고유 번호 목록")
        private List<Long> ingredientIds;
    }

    @Schema(description = "재료 직접 입력 요청 DTO")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketIngredientRequest {
        @Schema(description = "재료명")
        private String ingredientName;
        @Schema(description = "재료 아이콘 url")
        private String ingredientIconUrl;
        @Schema(description = "재료 카테고리 고유 번호")
        private Long ingredientCategoryId;
    }

    @Schema(description = "냉장고 바구니 수정 요청 DTO")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketRequest {
        @Schema(description = "유통기한")
        private LocalDate expiredAt;
        @Schema(description = "수량")
        private float quantity;
        @Schema(description = "단위")
        private String unit;

        public LocalDateTime getExpiredAt() {
            return this.expiredAt.atTime(LocalTime.MIN);
        }
    }

    @Schema(description = "냉장고 바구니 목록 응답 DTO")
    @Getter
    @Builder
    public static class FridgeBasketsResponse {
        @Schema(description = "냉장고 바구니 목록 총 갯수")
        private int fridgeBasketCount;
        @Schema(description = "재료 카테고리별 냉장고 바구니 목록")
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

    @Schema(description = "냉장고 바구니 재료 카테고리 응답 DTO")
    @Getter
    @Builder
    public static class FridgeBasketIngredientCategoryResponse {
        @Schema(description = "재료 카테고리 고유 번호")
        private Long ingredientCategoryId;
        @Schema(description = "재료 카테고리명")
        private String ingredientCategoryName;
        @Schema(description = "냉장고 바구니 목록")
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

    @Schema(description = "냉장고 바구니 응답 DTO")
    @Getter
    @Builder
    public static class FridgeBasketResponse {
        @Schema(description = "냉장고 바구니 고유 번호")
        private Long fridgeBasketId;
        @Schema(description = "냉장고 바구니 재료명")
        private String ingredientName;
        @Schema(description = "냉장고 바구니 재료 아이콘 url")
        private String ingredientIconUrl;
        @Schema(description = "냉장고 바구니 재료 유통 기한")
        private ZonedDateTime expiredAt;
        @Schema(description = "냉장고 바구니 재료 수량")
        private float quantity;
        @Schema(description = "냉장고 바구니 재료 단위")
        private String unit;

        public static FridgeBasketResponse from(FridgeBasket fridgeBasket) {
            return FridgeBasketResponse.builder()
                    .fridgeBasketId(fridgeBasket.getFridgeBasketId())
                    .ingredientName(fridgeBasket.getIngredient().getIngredientName())
                    .ingredientIconUrl(fridgeBasket.getIngredient().getIngredientIconUrl())
                    .expiredAt(fridgeBasket.getExpiredAt() != null ? fridgeBasket.getExpiredAt().atZone(ZoneId.of("Asia/Seoul")) : null)
                    .quantity(fridgeBasket.getQuantity())
                    .unit(fridgeBasket.getUnit())
                    .build();
        }
    }

    @Schema(description = "냉장고 바구니 갯수 응답 DTO")
    @Builder
    @Getter
    public static class FridgeBasketCountResponse {
        @Schema(description = "냉장고 바구니 갯수")
        private long fridgesBasketCount;

        public static FridgeBasketCountResponse from(long fridgesBasketCount) {
            return FridgeBasketCountResponse.builder()
                    .fridgesBasketCount(fridgesBasketCount)
                    .build();
        }
    }
}
