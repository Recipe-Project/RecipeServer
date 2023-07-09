package com.recipe.app.src.fridge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class GetFridgesRecipeRes {
    private final Integer total;
    private final List<RecipeList> recipeList;
}
