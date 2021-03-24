package com.recipe.app.src.fridge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class FridgeList {
    private final String ingredientName;
    private final String ingredientIcon;
    private final String expiredAt;
    private final String storageMethod;
    private final Integer count;
    private final Integer freshness;
}
