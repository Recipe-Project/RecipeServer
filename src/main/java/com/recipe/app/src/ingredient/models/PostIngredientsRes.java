package com.recipe.app.src.ingredient.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostIngredientsRes {
    private final List ingredientList;
}
