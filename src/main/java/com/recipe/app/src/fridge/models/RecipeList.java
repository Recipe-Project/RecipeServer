package com.recipe.app.src.fridge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class RecipeList {
    private final Integer recipeId;
    private final String title;
    private final String content;
    private final String thumbnail;
    private final String cookingTime;
    private final long scrapCount;
}
