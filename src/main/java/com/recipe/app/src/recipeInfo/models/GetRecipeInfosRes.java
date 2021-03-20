package com.recipe.app.src.recipeInfo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetRecipeInfosRes {
    private final Integer recipeId;
    private final String title;
    private final String description;
    private final String thumbnail;
    private final String UserScrapYN;
    private final Integer userScrapCnt;
}
