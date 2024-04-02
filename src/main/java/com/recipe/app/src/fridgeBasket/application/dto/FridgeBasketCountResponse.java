package com.recipe.app.src.fridgeBasket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "냉장고 바구니 갯수 응답 DTO")
@Getter
public class FridgeBasketCountResponse {

    @Schema(description = "냉장고 바구니 갯수")
    private final long fridgesBasketCount;

    @Builder
    public FridgeBasketCountResponse(long fridgesBasketCount) {
        this.fridgesBasketCount = fridgesBasketCount;
    }

    public static FridgeBasketCountResponse from(long fridgesBasketCount) {
        return FridgeBasketCountResponse.builder()
                .fridgesBasketCount(fridgesBasketCount)
                .build();
    }
}