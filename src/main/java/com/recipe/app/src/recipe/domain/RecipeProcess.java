package com.recipe.app.src.recipe.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RecipeProcess {

    private final Long recipeProcessId;
    private final Recipe recipe;
    private final int cookingNo;
    private final String cookingDescription;
    private final String recipeProcessImgUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public RecipeProcess(Long recipeProcessId, Recipe recipe, int cookingNo, String cookingDescription,
                         String recipeProcessImgUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.recipeProcessId = recipeProcessId;
        this.recipe = recipe;
        this.cookingNo = cookingNo;
        this.cookingDescription = cookingDescription;
        this.recipeProcessImgUrl = recipeProcessImgUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RecipeProcess from(Recipe recipe, int cookingNo, String cookingDescription, String recipeProcessImgUrl) {
        LocalDateTime now = LocalDateTime.now();
        return RecipeProcess.builder()
                .recipe(recipe)
                .cookingNo(cookingNo)
                .cookingDescription(cookingDescription)
                .recipeProcessImgUrl(recipeProcessImgUrl)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public RecipeProcess update(String cookingDescription, String recipeProcessImgUrl) {
        LocalDateTime now = LocalDateTime.now();
        return RecipeProcess.builder()
                .recipe(recipe)
                .cookingNo(cookingNo)
                .cookingDescription(cookingDescription)
                .recipeProcessImgUrl(recipeProcessImgUrl)
                .createdAt(createdAt)
                .updatedAt(now)
                .build();
    }
}