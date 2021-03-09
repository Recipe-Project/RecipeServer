package com.recipe.app.src.userRecipe.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class GetMyRecipeRes {
    private final List photoUrlList;
    private final String title;
    private final String content;
    private final List ingredientList;
}
