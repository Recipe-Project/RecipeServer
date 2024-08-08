package com.recipe.app.src.fridge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "냉장고 수정 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FridgeRequest {

    @Schema(description = "유통기한")
    private LocalDate expiredAt;
    @Schema(description = "수량")
    private float quantity;
    @Schema(description = "단위")
    private String unit;

    @Builder
    public FridgeRequest(LocalDate expiredAt, float quantity, String unit) {
        this.expiredAt = expiredAt;
        this.quantity = quantity;
        this.unit = unit;
    }
}
