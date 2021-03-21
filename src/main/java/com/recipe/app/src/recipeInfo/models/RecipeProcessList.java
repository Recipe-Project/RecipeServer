package com.recipe.app.src.recipeInfo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeProcessList {
    private final Integer recipeProcessIdx;
    private final Integer recipeProcessNo;
    private final String recipeProcessDc;
    private final String recipeProcessImg;
}
