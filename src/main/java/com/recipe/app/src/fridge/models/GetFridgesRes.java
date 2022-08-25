package com.recipe.app.src.fridge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class GetFridgesRes {
    private final long fridgeBasketCount;
    private final List<Fridges> fridges;

}
