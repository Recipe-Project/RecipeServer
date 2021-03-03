package com.recipe.app.src.userRecipe.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMyRecipesRes {
    private final Integer userRecipeIdx;
    private final String thumbnail;
    private final String title;
}
