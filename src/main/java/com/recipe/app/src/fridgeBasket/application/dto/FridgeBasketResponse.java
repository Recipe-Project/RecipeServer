package com.recipe.app.src.fridgeBasket.application.dto;

import com.recipe.app.src.fridge.domain.Freshness;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.Ingredient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Schema(description = "냉장고 바구니 응답 DTO")
@Getter
public class FridgeBasketResponse {

    @Schema(description = "냉장고 바구니 고유 번호")
    private final Long fridgeBasketId;
    @Schema(description = "냉장고 바구니 재료명")
    private final String ingredientName;
    @Schema(description = "냉장고 바구니 재료 아이콘 url")
    private final String ingredientIconUrl;
    @Schema(description = "냉장고 바구니 재료 유통 기한")
    private final ZonedDateTime expiredAt;
    @Schema(description = "냉장고 바구니 재료 수량")
    private final float quantity;
    @Schema(description = "냉장고 바구니 재료 단위")
    private final String unit;
    @Schema(description = "냉장고 바구니 신선도")
    private final String freshness;

    @Builder
    public FridgeBasketResponse(Long fridgeBasketId, String ingredientName, String ingredientIconUrl, ZonedDateTime expiredAt, float quantity, String unit, String freshness) {

        this.fridgeBasketId = fridgeBasketId;
        this.ingredientName = ingredientName;
        this.ingredientIconUrl = ingredientIconUrl;
        this.expiredAt = expiredAt;
        this.quantity = quantity;
        this.unit = unit;
        this.freshness = freshness;
    }

    public static FridgeBasketResponse from(FridgeBasket fridgeBasket, Ingredient ingredient) {
        return FridgeBasketResponse.builder()
                .fridgeBasketId(fridgeBasket.getFridgeBasketId())
                .ingredientName(ingredient.getIngredientName())
                .ingredientIconUrl(ingredient.getIngredientIconUrl())
                .expiredAt(fridgeBasket.getExpiredAt() != null ? fridgeBasket.getExpiredAt().atTime(LocalTime.MIN).atZone(ZoneId.of("Asia/Seoul")) : null)
                .quantity(fridgeBasket.getQuantity())
                .unit(fridgeBasket.getUnit())
                .freshness(fridgeBasket.getFreshness().getName())
                .build();
    }
}