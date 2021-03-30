package com.recipe.app.src.fridge.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class DeleteFridgesIngredientReq {
    private List<String> ingredientList;
}
