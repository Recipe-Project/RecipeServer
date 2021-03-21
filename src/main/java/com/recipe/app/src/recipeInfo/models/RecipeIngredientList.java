package com.recipe.app.src.recipeInfo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeIngredientList {
    private final Integer recipeIngredientIdx;
    private final String recipeIngredientName;
    private final String recipeIngredientCpcty;
}
