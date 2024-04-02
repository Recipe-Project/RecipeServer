package com.recipe.app.src.recipe.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "레시피 목록 응답 DTO")
@Getter
public class RecipesResponse {

    @Schema(description = "레시피 전체 갯수")
    private final long totalCnt;
    @Schema(description = "레시피 목록")
    private final List<RecipeResponse> recipes;

    @Builder
    public RecipesResponse(long totalCnt, List<RecipeResponse> recipes) {

        this.totalCnt = totalCnt;
        this.recipes = recipes;
    }
}
