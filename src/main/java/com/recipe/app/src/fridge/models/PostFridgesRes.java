package com.recipe.app.src.fridge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;


@Getter
@AllArgsConstructor
public class PostFridgesRes {
    private final String ingredientName;
    private final String ingredientIcon;
    private final Integer ingredientCategoryIdx;
    private final String expiredAt;
    private final String storageMethod;
    private final Integer count;
}
