package com.recipe.app.src.fridge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class PatchFridgeList {
    private final String ingredientName;
    private final String expiredAt;
    private final String storageMethod;
    private final Integer count;
}
