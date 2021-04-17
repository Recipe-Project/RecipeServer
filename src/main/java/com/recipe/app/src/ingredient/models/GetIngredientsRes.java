package com.recipe.app.src.ingredient.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class GetIngredientsRes {
    private final long fridgeBasketCount;
    private final List ingredients;
}