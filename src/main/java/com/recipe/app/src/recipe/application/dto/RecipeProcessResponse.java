package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.RecipeProcess;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "레시피 과정 응답 DTO")
@Getter
public class RecipeProcessResponse {

    @Schema(description = "레시피 과정 고유 번호")
    private final Long recipeProcessId;
    @Schema(description = "레시피 과정 순서")
    private final Integer recipeProcessNo;
    @Schema(description = "레시피 과정 설명")
    private final String recipeProcessDescription;
    @Schema(description = "레시피 과정 이미지 url")
    private final String recipeProcessImgUrl;

    @Builder
    public RecipeProcessResponse(Long recipeProcessId, Integer recipeProcessNo, String recipeProcessDescription, String recipeProcessImgUrl) {

        this.recipeProcessId = recipeProcessId;
        this.recipeProcessNo = recipeProcessNo;
        this.recipeProcessDescription = recipeProcessDescription;
        this.recipeProcessImgUrl = recipeProcessImgUrl;
    }

    public static RecipeProcessResponse from(RecipeProcess recipeProcess) {
        return RecipeProcessResponse.builder()
                .recipeProcessId(recipeProcess.getRecipeProcessId())
                .recipeProcessNo(recipeProcess.getCookingNo())
                .recipeProcessDescription(recipeProcess.getCookingDescription())
                .recipeProcessImgUrl(recipeProcess.getRecipeProcessImgUrl())
                .build();
    }
}