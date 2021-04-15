package com.recipe.app.src.userRecipe.models;




import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PatchMyRecipeRes {
    private final String thumbnail;
    private final String title;
    private final String content;
    private List ingredientList;
    private List<MyRecipeIngredient> directIngredientList;
}

