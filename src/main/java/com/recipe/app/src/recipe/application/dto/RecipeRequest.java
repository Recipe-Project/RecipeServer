package com.recipe.app.src.recipe.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "레시피 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeRequest {

    @Schema(description = "레시피 썸네일 이미지 url")
    private String thumbnail;
    @Schema(description = "레시피 제목")
    private String title;
    @Schema(description = "레시피 설명")
    private String content;
    @Schema(description = "레시피 재료 목록")
    private List<RecipeIngredientRequest> ingredients;
}
