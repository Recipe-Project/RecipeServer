package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.domain.RecipeLevel;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "레시피 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeRequest {

    @Schema(description = "레시피 썸네일 이미지 url")
    private String thumbnailImgUrl;
    @Schema(description = "레시피 제목")
    private String title;
    @Schema(description = "레시피 소개")
    private String introduction;
    @Schema(description = "요리 시간")
    private long cookingTime;
    @Schema(description = "난이도")
    private RecipeLevel level;
    @Schema(description = "공개 여부")
    private Boolean isHidden;
    @Schema(description = "레시피 재료 목록")
    private List<RecipeIngredientRequest> ingredients;
    @Schema(description = "레시피 과정 목록")
    private List<RecipeProcessRequest> processes;

    public Recipe toRecipeEntity(Long userId) {

        return Recipe.builder()
                .recipeNm(title)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .imgUrl(thumbnailImgUrl)
                .isHidden(isHidden)
                .userId(userId)
                .build();
    }

    public List<RecipeIngredient> toRecipeIngredientEntities(Long recipeId) {

        return ingredients.stream()
                .map(ingredient -> ingredient.toEntity(recipeId))
                .collect(Collectors.toList());
    }

    public List<RecipeProcess> toRecipeProcessEntities(Long recipeId) {

        return processes.stream()
                .map(process -> process.toEntity(recipeId))
                .collect(Collectors.toList());
    }
}
