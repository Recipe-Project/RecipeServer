package com.recipe.app.src.recipeInfo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetRecipeInfoRes {
    private final Integer recipeId;
    private final String recipeName;
    private final String summary;
    private final String thumbnail;
    private final String cookingTime;
    private final String level;
    private final List<RecipeIngredientList> recipeIngredientList;
    private final List<RecipeProcessList> recipeProcessList;
    private final String userScrapYN;
    private final Integer userScrapCnt;
}
