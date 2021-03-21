package com.recipe.app.src.fridgeBasket.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class PostFridgesBasketRes {
    private final String ingredientName;
    private final String ingredientIcon;
    private final Integer ingredientCategoryIdx;
}
