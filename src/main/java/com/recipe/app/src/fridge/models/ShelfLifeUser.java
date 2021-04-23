package com.recipe.app.src.fridge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ShelfLifeUser {
    private final String deviceToken;
    private final String ingredientName;
}
