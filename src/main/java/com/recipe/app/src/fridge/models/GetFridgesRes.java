package com.recipe.app.src.fridge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;


@Getter
@AllArgsConstructor
public class GetFridgesRes {
    private final Integer ingredientCategoryIdx;
    private final String ingredientCategoryName;
    private final List fridgeList;

}
