package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "레시피 과정 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeProcessRequest {

    @Schema(description = "레시피 과정 순서")
    private int cookingNo;
    @Schema(description = "레시피 과정 설명")
    private String cookingDescription;
    @Schema(description = "레시피 과정 이미지 url")
    private String recipeProcessImgUrl;

    public RecipeProcess toEntity(Recipe recipe) {

        return RecipeProcess.builder()
                .recipe(recipe)
                .cookingNo(cookingNo)
                .cookingDescription(cookingDescription)
                .recipeProcessImgUrl(recipeProcessImgUrl)
                .build();
    }
}
