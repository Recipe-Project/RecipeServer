package com.recipe.app.src.fridge.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PostFridgesReq {
    private List<FridgeBasketList> fridgeBasketList;
}