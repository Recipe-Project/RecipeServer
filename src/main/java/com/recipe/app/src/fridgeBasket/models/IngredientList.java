package com.recipe.app.src.fridgeBasket.models;

import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public class IngredientList {
    private final Integer ingredientIdx;
    private final String ingredientName;
    private final String ingredientIcon;
    private final Integer ingredientCategoryIdx;
    private final Integer ingredientCnt;
    private final String storageMethod;
    private final String expiredAt;
}