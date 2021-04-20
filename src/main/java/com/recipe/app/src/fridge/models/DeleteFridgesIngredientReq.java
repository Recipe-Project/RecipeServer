package com.recipe.app.src.fridge.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class DeleteFridgesIngredientReq {
    private String ingredientName;
}
