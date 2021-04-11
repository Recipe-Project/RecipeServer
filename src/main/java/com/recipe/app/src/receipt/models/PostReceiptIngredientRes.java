package com.recipe.app.src.receipt.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostReceiptIngredientRes {
    private final Integer ingredientIdx;
    private final String ingredientName;
    private final String ingredientIcon;
    private final Integer ingredientCategoryIdx;

}
