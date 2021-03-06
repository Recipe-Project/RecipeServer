package com.recipe.app.src.ingredient.models;

import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public class IngredientList {
    private final Integer ingredientIdx;
    private final String ingredientName;
    private final String ingredientIcon;
}