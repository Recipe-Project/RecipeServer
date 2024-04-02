package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "레시피 상세 응답 DTO")
@Getter
public class RecipeDetailResponse {

    @Schema(description = "레시피 고유 번호")
    private final Long recipeId;
    @Schema(description = "레시피명")
    private final String recipeName;
    @Schema(description = "소개글")
    private final String introduction;
    @Schema(description = "썸네일 이미지 url")
    private final String thumbnailImgUrl;
    @Schema(description = "조리 시간")
    private final Long cookingTime;
    @Schema(description = "난이도")
    private final String level;
    @Schema(description = "레시피 재료 목록")
    private final List<RecipeIngredientResponse> recipeIngredients;
    @Schema(description = "레시피 과정 목록")
    private final List<RecipeProcessResponse> recipeProcesses;
    @Schema(description = "스크랩 여부")
    private final Boolean isUserScrap;
    @Schema(description = "총 스크랩수")
    private final long scrapCnt;
    @Schema(description = "총 조회수")
    private final long viewCnt;

    @Builder
    public RecipeDetailResponse(Long recipeId, String recipeName, String introduction, String thumbnailImgUrl, Long cookingTime, String level,
                                List<RecipeIngredientResponse> recipeIngredients, List<RecipeProcessResponse> recipeProcesses,
                                Boolean isUserScrap, long scrapCnt, long viewCnt) {

        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.introduction = introduction;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.cookingTime = cookingTime;
        this.level = level;
        this.recipeIngredients = recipeIngredients;
        this.recipeProcesses = recipeProcesses;
        this.isUserScrap = isUserScrap;
        this.scrapCnt = scrapCnt;
        this.viewCnt = viewCnt;
    }

    public static RecipeDetailResponse from(Recipe recipe, List<RecipeIngredientResponse> recipeIngredients, List<RecipeProcessResponse> recipeProcesses,
                                            boolean isUserScrap, long scrapCnt, long viewCnt) {

        return RecipeDetailResponse.builder()
                .recipeId(recipe.getRecipeId())
                .recipeName(recipe.getRecipeNm())
                .introduction(recipe.getIntroduction())
                .thumbnailImgUrl(recipe.getImgUrl())
                .cookingTime(recipe.getCookingTime())
                .level(recipe.getLevel().getName())
                .recipeIngredients(recipeIngredients)
                .recipeProcesses(recipeProcesses)
                .isUserScrap(isUserScrap)
                .scrapCnt(scrapCnt)
                .viewCnt(viewCnt)
                .build();
    }
}