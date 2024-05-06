package com.recipe.app.src.fridge.application.dto;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.ingredient.domain.Ingredient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Schema(description = "냉장고 응답 DTO")
@Getter
public class FridgeResponse {

    @Schema(description = "냉장고 고유 번호")
    private final Long fridgeId;
    @Schema(description = "재료명")
    private final String ingredientName;
    @Schema(description = "재료 아이콘 고유 번호")
    private final Long ingredientIconId;
    @Schema(description = "유통기한")
    private final ZonedDateTime expiredAt;
    @Schema(description = "수량")
    private final float quantity;
    @Schema(description = "단위")
    private final String unit;
    @Schema(description = "신선도")
    private final String freshness;

    @Builder
    public FridgeResponse(Long fridgeId, String ingredientName, Long ingredientIconId, ZonedDateTime expiredAt, float quantity, String unit, String freshness) {

        this.fridgeId = fridgeId;
        this.ingredientName = ingredientName;
        this.ingredientIconId = ingredientIconId;
        this.expiredAt = expiredAt;
        this.quantity = quantity;
        this.unit = unit;
        this.freshness = freshness;
    }

    public static FridgeResponse from(Fridge fridge, Ingredient ingredient) {
        return FridgeResponse.builder()
                .fridgeId(fridge.getFridgeId())
                .ingredientName(ingredient.getIngredientName())
                .ingredientIconId(ingredient.getIngredientIconId())
                .expiredAt(fridge.getExpiredAt() != null ? fridge.getExpiredAt().atTime(LocalTime.MIN).atZone(ZoneId.of("Asia/Seoul")) : null)
                .quantity(fridge.getQuantity())
                .unit(fridge.getUnit())
                .freshness(fridge.getFreshness().getName())
                .build();
    }
}