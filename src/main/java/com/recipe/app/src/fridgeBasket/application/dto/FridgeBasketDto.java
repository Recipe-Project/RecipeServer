package com.recipe.app.src.fridgeBasket.application.dto;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.fridgeBasket.models.FridgeBasketList;
import com.recipe.app.src.fridgeBasket.models.IngredientList;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class FridgeBasketDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketIdsRequest {
        private List<Integer> ingredientList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DirectFridgeBasketsRequest {
        private String ingredientName;
        private String ingredientIcon;
        private Integer ingredientCategoryIdx;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketsRequest {
        List<FridgeBasketRequest> fridgeBasketList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketRequest {
        private String ingredientName ;
        private Integer ingredientCnt;
        private String storageMethod;
        private String expiredAt;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DirectFridgeBasketsResponse {
        private String ingredientName;
        private String ingredientIcon;
        private Integer ingredientCategoryIdx;

        public DirectFridgeBasketsResponse(FridgeBasket fridgeBasket) {
            this(fridgeBasket.getIngredientName(), fridgeBasket.getIngredientIcon(), fridgeBasket.getIngredientCategory().getIngredientCategoryIdx());
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketsResponse {
        private Long ingredientCount;
        private List<FridgeBasketResponse> ingredientList;

        public FridgeBasketsResponse(long ingredientCount, List<FridgeBasket> fridgeBaskets) {
            this.ingredientCount = ingredientCount;
            this.ingredientList = fridgeBaskets.stream()
                    .map(FridgeBasketResponse::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketResponse {
        private Integer ingredientIdx;
        private String ingredientName;
        private String ingredientIcon;
        private Integer ingredientCategoryIdx;
        private Integer ingredientCnt;
        private String storageMethod;
        private String expiredAt;

        public FridgeBasketResponse(FridgeBasket fridgeBasket) {
            this(fridgeBasket.getIngredient() != null ? fridgeBasket.getIngredient().getIngredientIdx() : null,
                    fridgeBasket.getIngredientName(),
                    fridgeBasket.getIngredientIcon(),
                    fridgeBasket.getIngredientCategory().getIngredientCategoryIdx(),
                    fridgeBasket.getCount(),
                    fridgeBasket.getStorageMethod(),
                    fridgeBasket.getExpiredAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "까지");
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FridgeBasketsCountResponse {
        private long fridgesBasketCount;
    }
}
