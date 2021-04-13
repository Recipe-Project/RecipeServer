package com.recipe.app.src.fridge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ShelfLifeUser {
    private final Integer userIdx; //디바이스토큰으로
    private final String ingredientName;
}
