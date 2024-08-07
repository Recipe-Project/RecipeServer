package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Builder
    public RecipeRequest(String thumbnailImgUrl, String title, String introduction, long cookingTime, RecipeLevel level,
                         Boolean isHidden, List<RecipeIngredientRequest> ingredients, List<RecipeProcessRequest> processes) {
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.title = title;
        this.introduction = introduction;
        this.cookingTime = cookingTime;
        this.level = level;
        this.isHidden = isHidden;
        this.ingredients = ingredients;
        this.processes = processes;
    }

    public Recipe toRecipeEntity(Long userId) {

        Recipe recipe = Recipe.builder()
                .recipeNm(title)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .imgUrl(thumbnailImgUrl)
                .isHidden(isHidden)
                .userId(userId)
                .build();

        ingredients.forEach(ingredient -> ingredient.toEntity(recipe));
        processes.forEach(process -> process.toEntity(recipe));

        return recipe;
    }
}
