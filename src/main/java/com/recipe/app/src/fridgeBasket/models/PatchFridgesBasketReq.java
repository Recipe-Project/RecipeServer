package com.recipe.app.src.fridgeBasket.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PatchFridgesBasketReq {
    List<FridgeBasketList> fridgeBasketList;
}
