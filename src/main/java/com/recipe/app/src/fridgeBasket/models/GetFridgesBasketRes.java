package com.recipe.app.src.fridgeBasket.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetFridgesBasketRes {
    private final Long ingredientCount;
    private final List ingredientList;
}
