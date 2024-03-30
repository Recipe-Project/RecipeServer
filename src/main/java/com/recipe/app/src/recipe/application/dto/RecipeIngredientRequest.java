package com.recipe.app.src.recipe.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "레시피 재료 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeIngredientRequest {

    @Schema(description = "재료 고유 번호 (재료 선택 시 추가, 직접 재료 입력 시 null)", nullable = true)
    private Long ingredientId;
    @Schema(description = "레시피 재료명 (직접 재료 입력 시 추가, 재료 선택 시 null)", nullable = true)
    private String ingredientName;
    @Schema(description = "레시피 재료 아이콘 url (직접 재료 입력 시 추가, 재료 선택 시 null)", nullable = true)
    private String ingredientIconUrl;
    @Schema(description = "레시피 재료 카테고리 고유 번호 (직접 재료 입력 시 추가, 재료 선택 시 null)", nullable = true)
    private Long ingredientCategoryId;
    @Schema(description = "레시피 재료 용량", nullable = true)
    private String capacity;
}